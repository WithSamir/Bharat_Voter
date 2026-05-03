#!/bin/bash
# deploy.sh
# Automates the setup of IAM roles and mapping .env variables to GCP Secret Manager

# Ensure the script exits on any failure
set -e

# Load environment variables from .env
if [ -f .env ]; then
  export $(grep -v '^#' .env | xargs)
else
  echo ".env file not found! Make sure you are running this in the project root."
  exit 1
fi

PROJECT_ID=${GCP_PROJECT_ID}
if [ -z "$PROJECT_ID" ] || [ "$PROJECT_ID" == "your_gcp_project_id" ]; then
    echo "Please set a valid GCP_PROJECT_ID in your .env file."
    exit 1
fi

SERVICE_ACCOUNT="election-run-sa@${PROJECT_ID}.iam.gserviceaccount.com"

echo "Setting up GCP Project: $PROJECT_ID"
gcloud config set project $PROJECT_ID

echo "Enabling necessary APIs..."
gcloud services enable \
    run.googleapis.com \
    secretmanager.googleapis.com \
    cloudbuild.googleapis.com \
    discoveryengine.googleapis.com \
    translate.googleapis.com \
    documentai.googleapis.com \
    aiplatform.googleapis.com

echo "Creating Service Account: $SERVICE_ACCOUNT..."
gcloud iam service-accounts create election-run-sa \
    --display-name="Service Account for Election Dashboard Cloud Run" || true

echo "Granting IAM Roles for Gemini, Translation, Document AI, and Vertex AI Search..."
ROLES=(
    "roles/aiplatform.user"               # For Gemini / Vertex AI
    "roles/cloudtranslate.user"           # For Translation API
    "roles/discoveryengine.editor"        # For Vertex AI Search (RAG)
    "roles/documentai.viewer"             # For Document AI processing
    "roles/secretmanager.secretAccessor"  # To access Secret Manager
)

for ROLE in "${ROLES[@]}"; do
    gcloud projects add-iam-policy-binding $PROJECT_ID \
        --member="serviceAccount:$SERVICE_ACCOUNT" \
        --role="$ROLE"
done

echo "Mapping .env variables to GCP Secret Manager..."
# Array of variables to sync from .env
SECRETS=(
    "GCP_PROJECT_ID"
    "GCP_REGION"
    "VERTEX_SEARCH_DATASTORE_ID"
    "DOCAI_PROCESSOR_ID"
    "GEMINI_API_KEY"
    "GOOGLE_MAPS_API_KEY"
)

for SECRET in "${SECRETS[@]}"; do
    # Fetch the value from the current environment
    SECRET_VALUE=$(printenv $SECRET)
    
    if [ -z "$SECRET_VALUE" ] || [ "$SECRET_VALUE" == "your_gemini_api_key" ]; then
        echo "Warning: $SECRET is empty or placeholder in .env. Skipping..."
        continue
    fi

    echo "Creating and adding version for secret: $SECRET"
    # Create secret if it doesn't exist
    gcloud secrets create $SECRET --replication-policy="automatic" || true
    
    # Add the value as a new secret version
    echo -n "$SECRET_VALUE" | gcloud secrets versions add $SECRET --data-file=-
    
    # Grant Cloud Run service account access to this secret
    gcloud secrets add-iam-policy-binding $SECRET \
        --member="serviceAccount:$SERVICE_ACCOUNT" \
        --role="roles/secretmanager.secretAccessor"
done

echo "Setup complete!"
echo "To deploy your application, run:"
echo "gcloud builds submit --config cloudbuild.yaml ."
