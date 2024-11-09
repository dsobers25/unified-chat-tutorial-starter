package com.example.application.views.testpkg;

import java.util.Arrays;
import java.util.List;

import com.example.application.views.MainLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.PermitAll;

// @Route(value = "") 
@Route(value = "nonono", layout = MainLayout.class)
@PageTitle("Test BItch")
@PermitAll
public class TesterPage1 extends VerticalLayout {

    private static class AlignmentOption {
        private final String label;
        private final FlexComponent.Alignment alignment;

        public AlignmentOption(String label,
                FlexComponent.Alignment alignment) {
            this.label = label;
            this.alignment = alignment;
        }

        public FlexComponent.Alignment getAlignment() {
            return alignment;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    public TesterPage1() {
        // tag::layout[]
        Div myDiv = new Div();
        myDiv.setId("my-div");
        myDiv.setWidth("200px");
        myDiv.setHeight("200px");
        myDiv.setText("Hello");
        myDiv.addClassName("random");
        Div myDiv2 = new Div();
        Div myDiv3 = new Div();
        myDiv2.add(new H1("Hey"));
        myDiv3.add(new H1("Hi"));
        HorizontalLayout layout = new HorizontalLayout();
        layout.setPadding(true);
        layout.add(myDiv);
        layout.add(myDiv2);
        layout.add(myDiv3);

        HorizontalLayout layout2 = new HorizontalLayout();
        Div newDiv = new Div();
        newDiv.add(new H1("I'm on a new Layout!"));
        // newDiv.addClassName("random2");
        // newDiv.getStyle().set("background-image", "url('src/main/resources/META-INF/resources/frontend/images/wooden_human_1.jpg')");
        // I was able to add an image in a div via java code
        newDiv.getStyle().set("background-image", "url('/frontend/images/wooden_human_1_v2.jpg')");
        newDiv.getStyle().set("background-size", "cover");
        newDiv.getStyle().set("width", "100%");
        newDiv.setHeight("350px");
        layout2.add(newDiv);
        // end::layout[]

        List<AlignmentOption> options = Arrays
                .asList(new AlignmentOption("Stretch (default)",
                                FlexComponent.Alignment.STRETCH),
                        new AlignmentOption("Start",
                                FlexComponent.Alignment.START),
                        new AlignmentOption("Center",
                                FlexComponent.Alignment.CENTER),
                        new AlignmentOption("End", FlexComponent.Alignment.END),
                        new AlignmentOption("Baseline",
                                FlexComponent.Alignment.BASELINE));

        RadioButtonGroup<AlignmentOption> radioGroup = new RadioButtonGroup<>();
        radioGroup.setLabel("Vertical alignment");
        radioGroup.setItems(options);
        radioGroup.setValue(options.get(0));
        radioGroup.addValueChangeListener(e -> {
            FlexComponent.Alignment alignment = e.getValue().getAlignment();
            layout.setAlignItems(alignment);
        });

        this.setClassName("basic-layouts-example");
        layout.setClassName("height-5xl");

        this.add(layout, radioGroup, layout2);
    }

}
