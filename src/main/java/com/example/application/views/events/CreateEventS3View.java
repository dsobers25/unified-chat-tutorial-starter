package com.example.application.views.events;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import com.example.application.events.Event;
import com.example.application.events.EventService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.PermitAll;

// @Route("create-event")
@PermitAll
@Route(value = "createeventS3", layout = MainLayout.class)
public class CreateEventS3View extends VerticalLayout {

    private Upload upload;
    private Image previewImage;
    private TextField titleField;
    private TextArea detailsArea;
    private DatePicker eventDate;  // Added date picker
    private Button saveButton;
    private MemoryBuffer buffer;
    private EventService eventService;
    private Event currentEvent = null;
    
    // List to store events (you might want to move this to a service class)
    private static List<Event> events = new ArrayList<>();

    public CreateEventView(EventService eventService) {
        initializeComponents();
    
        this.eventService = eventService;
        setAlignItems(Alignment.CENTER);
        setSpacing(true);
        setPadding(true);
    
        createImageUpload();
        createFormFields();
        createButtons();  // Single method to handle all buttons
    
        add(
            new H2("Create/Edit Event"),
            upload,
            previewImage,
            titleField,
            eventDate,
            detailsArea,
            createEventsList()
        );
    }

     private void initializeComponents() {
        // Initialize buffer and upload
        buffer = new MemoryBuffer();
        upload = new Upload(buffer);
        
        // Initialize image
        previewImage = new Image();
        previewImage.setVisible(false);
        previewImage.setMaxWidth("300px");

        // Initialize form fields
        titleField = new TextField("Event Title");
        titleField.setWidthFull();
        titleField.setRequired(true);
        titleField.setMaxLength(100);

        eventDate = new DatePicker("Event Date");
        eventDate.setRequired(true);
        eventDate.setMin(LocalDate.now());
        eventDate.setMax(LocalDate.now().plusYears(1));
        eventDate.setWidthFull();

        detailsArea = new TextArea("Event Details");
        detailsArea.setWidthFull();
        detailsArea.setMinHeight("150px");
        detailsArea.setRequired(true);
        detailsArea.setMaxLength(1000);

        // Initialize save button
        saveButton = new Button("Create Event", e -> saveEvent());
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        // Set up upload listener
        upload.addSucceededListener(event -> {
            String imageUrl = createImageUrl(buffer.getInputStream());
            previewImage.setSrc(imageUrl);
            previewImage.setVisible(true);
        });
    }

    private String createImageUrl(InputStream inputStream) {
        try {
            byte[] bytes = inputStream.readAllBytes();
            String base64Image = Base64.getEncoder().encodeToString(bytes);
            return "data:image/jpeg;base64," + base64Image;
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    private void createFormFields() {
        titleField = new TextField("Event Title");
        titleField.setWidthFull();
        titleField.setRequired(true);
        titleField.setMaxLength(100);
        add(titleField);

        // Initialize date picker
        eventDate = new DatePicker("Event Date");
        eventDate.setRequired(true);
        eventDate.setMin(LocalDate.now()); // Can't create events in the past
        eventDate.setMax(LocalDate.now().plusYears(1)); // Limit to 1 year in future
        eventDate.setWidthFull();
        add(eventDate);

        detailsArea = new TextArea("Event Details");
        detailsArea.setWidthFull();
        detailsArea.setMinHeight("150px");
        detailsArea.setRequired(true);
        detailsArea.setMaxLength(1000);
        add(detailsArea);
    }

    private Component createEventsList() {
    Grid<Event> grid = new Grid<>(Event.class);
    grid.setItems(events);
    
    // Configure grid columns
    grid.setColumns("title", "date", "details");
    grid.getColumnByKey("title").setHeader("Event Title");
    grid.getColumnByKey("date").setHeader("Date");
    grid.getColumnByKey("details").setHeader("Details");
    
    // Add image column
    grid.addComponentColumn(event -> {
        Image img = new Image(event.getImageUrl(), "Event image");
        img.setHeight("50px");
        return img;
    }).setHeader("Image");

    // Add actions column with edit and delete buttons
    grid.addComponentColumn(event -> {
        // Create button layout
        HorizontalLayout buttonLayout = new HorizontalLayout();
        
        // Edit button
        Button editButton = new Button("Edit");
        editButton.addThemeVariants(ButtonVariant.LUMO_SMALL);
        editButton.addClickListener(e -> populateForm(event));
        
        // Delete button
        Button deleteButton = new Button("Delete");
        deleteButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ERROR);
        deleteButton.addClickListener(e -> {
            // Add confirmation dialog
            ConfirmDialog dialog = new ConfirmDialog();
            dialog.setHeader("Delete Event");
            dialog.setText("Are you sure you want to delete this event?");
            
            dialog.setCancelable(true);
            dialog.setCancelText("Cancel");
            
            dialog.setConfirmText("Delete");
            dialog.addConfirmListener(event2 -> deleteEvent(event));
            
            dialog.open();
        });
        
        buttonLayout.add(editButton, deleteButton);
        return buttonLayout;
    }).setHeader("Actions");

    return grid;
}

private void saveEvent() {
    if (!validateForm()) {
        return;
    }

    try {
        // Check if we're updating or creating
        if (currentEvent != null && saveButton.getText().equals("Update Event")) {
            // Delete the old event using the same deletion logic
            deleteEvent(currentEvent);
            
            // Create and add the updated event
            Event updatedEvent = new Event(
                titleField.getValue(),
                detailsArea.getValue(),
                previewImage.getSrc(),
                eventDate.getValue()
            );
            events.add(updatedEvent);
            
            Notification.show("Event updated successfully!", 
                3000, Position.TOP_CENTER);
        } else {
            // Create new event
            Event newEvent = new Event(
                titleField.getValue(),
                detailsArea.getValue(),
                previewImage.getSrc(),
                eventDate.getValue()
            );
            events.add(newEvent);
            
            Notification.show("Event created successfully!", 
                3000, Position.TOP_CENTER);
        }
        
        // Clear form and reset state
        clearForm();
        currentEvent = null;
        saveButton.setText("Create Event");

        // Refresh the view to show changes
        UI.getCurrent().getPage().reload();

    } catch (Exception e) {
        Notification.show("Error saving event: " + e.getMessage(), 
            3000, Position.TOP_CENTER);
    }
}

    private boolean validateForm() {
        boolean isValid = true;

        if (titleField.isEmpty()) {
            titleField.setInvalid(true);
            isValid = false;
        }

        if (detailsArea.isEmpty()) {
            detailsArea.setInvalid(true);
            isValid = false;
        }

        if (eventDate.isEmpty()) {  // Added date validation
            eventDate.setInvalid(true);
            isValid = false;
        }

        if (!previewImage.isVisible()) {
            Notification.show("Please upload an image");
            isValid = false;
        }

        return isValid;
    }

    private void clearForm() {
        titleField.clear();
        detailsArea.clear();
        eventDate.clear();
        previewImage.setVisible(false);
        upload.clearFileList();
        currentEvent = null;
        saveButton.setText("Create Event");
    }

    // ... rest of your existing methods ...
    private void createImageUpload() {
        buffer = new MemoryBuffer();
        upload = new Upload(buffer);
        previewImage = new Image();
        previewImage.setVisible(false);
        previewImage.setMaxWidth("300px");
    
        // Configure upload settings
        upload.setAcceptedFileTypes("image/*");
        upload.setMaxFiles(1);
        upload.setMaxFileSize(5 * 1024 * 1024); // 5MB limit
    
        // Add upload success listener
        upload.addSucceededListener(event -> {
            try {
                // Create preview
                String imageUrl = createImageUrl(buffer.getInputStream());
                previewImage.setSrc(imageUrl);
                previewImage.setVisible(true);
            } catch (Exception e) {
                Notification.show("Error processing image: " + e.getMessage(),
                    3000, Notification.Position.TOP_CENTER);
            }
        });
    
        // Add failure listener
        upload.addFailedListener(event -> {
            Notification.show("Upload failed: " + event.getReason().getMessage(),
                3000, Notification.Position.TOP_CENTER);
        });
    
        // Add file reject listener
        upload.addFileRejectedListener(event -> {
            Notification.show("File rejected: " + event.getErrorMessage(),
                3000, Notification.Position.TOP_CENTER);
        });
    }

    private void createButtons() {
        // Create save button
        saveButton = new Button("Create Event");
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.setWidthFull();
        saveButton.addClickListener(event -> saveEvent());
    
        // Create cancel button
        Button cancelButton = new Button("Cancel");
        cancelButton.addClickListener(e -> clearForm());
        
        // Create a horizontal layout for buttons
        HorizontalLayout buttonLayout = new HorizontalLayout(saveButton, cancelButton);
        buttonLayout.setWidthFull();
        add(buttonLayout);
    }

private void populateForm(Event event) {
    currentEvent = event; // Store the current event being edited
    titleField.setValue(event.getTitle());
    detailsArea.setValue(event.getDetails());
    eventDate.setValue(event.getDate());
    if (event.getImageUrl() != null) {
        previewImage.setSrc(event.getImageUrl());
        previewImage.setVisible(true);
    }
    
    // Change save button text to indicate editing
    saveButton.setText("Update Event");
}

private void deleteEvent(Event event) {
    try {
        events.remove(event);
        Notification.show("Event deleted successfully!", 
            3000, Position.TOP_CENTER);
        UI.getCurrent().getPage().reload();
    } catch (Exception e) {
        Notification.show("Error deleting event: " + e.getMessage(),
            3000, Position.TOP_CENTER);
    }
}

}