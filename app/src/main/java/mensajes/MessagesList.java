package mensajes;

public class MessagesList {

    private String name, email, lastMessage, profilePicture, chatKey;
    private int unseenMessages;

    public MessagesList(String name, String email, String lastMessage, String ProfilePicture, int unseenMessages, String chatKey) {
        this.name = name;
        this.email = email;
        this.lastMessage = lastMessage;
        this.profilePicture = ProfilePicture;
        this.unseenMessages = unseenMessages;
        this.chatKey = chatKey;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public String getChatKey() {
        return chatKey;
    }

    public int getUnseenMessages() {
        return unseenMessages;
    }
}
