package com.election.dashboard;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Meta;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

/**
 * AppShell – Configures the HTML shell of the Vaadin application.
 *
 * FIX 1: @Theme(variant = Lumo.DARK) enables Vaadin's built-in dark mode,
 * which sets all component text colours to white/light — making text visible
 * on the dark backgrounds we use throughout the app.
 *
 * This is the ONLY correct way to enable Lumo dark mode in Vaadin Flow 24.
 */
@Theme(variant = Lumo.DARK)
@PWA(name = "Bharat Voter Portal", shortName = "BVP",
     description = "Election Commission of India — Digital Services Portal")
@Meta(name = "color-scheme", content = "dark")
public class AppShell implements AppShellConfigurator {
}
