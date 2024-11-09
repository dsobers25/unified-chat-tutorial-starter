package com.example.application.views.channel;

import java.util.Comparator;
import java.util.List;

import com.example.application.chat.ChatService;
import com.example.application.chat.Message;
import com.example.application.util.LimitedSortedAppendOnlyList;
import com.example.application.views.MainLayout;
import com.example.application.views.lobby.LobbyView;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.messages.MessageInput;
import com.vaadin.flow.component.messages.MessageList;
import com.vaadin.flow.component.messages.MessageListItem;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.security.AuthenticationContext;
import com.vaadin.flow.theme.lumo.LumoUtility;

import jakarta.annotation.security.PermitAll;
import reactor.core.Disposable;

@PermitAll
@Route(value = "channel", layout = MainLayout.class)
public class ChannelView extends VerticalLayout 
        implements HasUrlParameter<String>, HasDynamicTitle { 
            private String channelName;

    private final ChatService chatService;
    private final MessageList messageList;
    private String channelId;
    // private final List<Message> receivedMessages = new ArrayList<>();
    private static final int HISTORY_SIZE = 20; 
    private final LimitedSortedAppendOnlyList<Message> receivedMessages;
    private final String currentUserName;


    public ChannelView(ChatService chatService,
    AuthenticationContext authenticationContext) {
        this.currentUserName = authenticationContext.getPrincipalName().orElseThrow();
    this.chatService = chatService;
    receivedMessages = new LimitedSortedAppendOnlyList<>(
            HISTORY_SIZE, 
            Comparator.comparing(Message::sequenceNumber)
    );
// tag::snippet[]
    setSizeFull(); 

    messageList = new MessageList();
    messageList.addClassNames(LumoUtility.Border.ALL);
    messageList.setSizeFull();
    add(messageList);

    var messageInput = new MessageInput(event -> sendMessage(event.getValue()));
    messageInput.setWidthFull();
    add(messageInput);



// end::snippet[]
}




// It’s a good practice to put the user interface logic in private methods rather than inside event listeners.
// @Override
// public void setParameter(BeforeEvent event, String channelId) {
// tag::snippet[]

// if (chatService.channel(channelId).isEmpty()) {
//     throw new IllegalArgumentException("Invalid channel ID Bitch"); 
// }

// if (channel.isEmpty()) {
//     Notification.show("Channel not found. Redirecting to main page.");
//     event.getUI().navigate("");
//     return;
// }
// this.channelId = channelId;
// end::snippet[]
// }

// on second view - https://vaadin.com/docs/latest/getting-started/tutorial/flow/second-view
// @Override
// public void setParameter(BeforeEvent event, String channelId) {
// // tag::snippet[]
//     if (chatService.channel(channelId).isEmpty()) {
//         event.forwardTo(LobbyView.class); 
//     } else {
//         this.channelId = channelId;
//     }
// // end::snippet[]
// }

// on add a layout - https://vaadin.com/docs/latest/getting-started/tutorial/flow/layout
@Override
public void setParameter(BeforeEvent event, String channelId) {
// tag::snippet[]
    chatService.channel(channelId).ifPresentOrElse(
            channel -> this.channelName = channel.name(), 
            () -> event.forwardTo(LobbyView.class) 
    );
    this.channelId = channelId;
// end::snippet[]
}

// In a future iteration, you'll navigate away from this view if the channel ID is invalid. For now, throwing an exception as shown here is enough.
private void sendMessage(String message) {
    if (!message.isBlank()) {
        chatService.postMessage(channelId, message);
    }
}

@Override
public String getPageTitle() {
    return channelName;
}


private MessageListItem createMessageListItem(Message message) {
    var item = new MessageListItem(
        message.message(),
        message.timestamp(),
        message.author()
    );
    item.setUserColorIndex(Math.abs(message.author().hashCode() % 7));
    item.addClassNames(LumoUtility.Margin.SMALL, LumoUtility.BorderRadius.MEDIUM); 
    if (message.author().equals(currentUserName)) {
        item.addClassNames(LumoUtility.Background.CONTRAST_5); 
    }
    return item;
}

// this method gets called everytime a message arrives
private void receiveMessages(List<Message> incoming) { 
    getUI().ifPresent(ui -> ui.access(() -> { 
        receivedMessages.addAll(incoming);
        messageList.setItems(receivedMessages.stream()
            .map(this::createMessageListItem)
            .toList()); 
    }));
}

// private Disposable subscribe() {
//     var subscription = chatService
//             .liveMessages(channelId)
//             .subscribe(this::receiveMessages); 
//     return subscription; 
// }
// Add Message History - https://vaadin.com/docs/latest/getting-started/tutorial/flow/message-history

private Disposable subscribe() {
    var subscription = chatService
            .liveMessages(channelId)
            .subscribe(this::receiveMessages);
// tag::snippet[]
    var lastSeenMessageId = receivedMessages.getLast() 
        .map(Message::messageId).orElse(null); 
    receiveMessages(chatService.messageHistory(
        channelId, 
        HISTORY_SIZE, 
        lastSeenMessageId
    ));
// end::snippet[]
    return subscription;
}

@Override
protected void onAttach(AttachEvent attachEvent) {
    var subscription = subscribe(); 
    addDetachListener(event -> subscription.dispose()); 
}


}