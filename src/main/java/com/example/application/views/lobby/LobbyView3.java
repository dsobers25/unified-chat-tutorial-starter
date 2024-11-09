package com.example.application.views.lobby;

import com.example.application.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.PermitAll;

// @Route(value = "") 
@Route(value = "no", layout = MainLayout.class)
@PageTitle("Lobby")
@PermitAll
public class LobbyView3 extends VerticalLayout {

    private final Button tes;

    public LobbyView3() {
        // addChannelButton = new Button("Add channel");
        this.tes = new Button("Test button", event -> System.out.println("I was pressed!"));
    
        var toolbar = new HorizontalLayout(tes); 
        add(toolbar);
    
        }
}