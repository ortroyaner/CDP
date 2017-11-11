package ex1;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Or Troyaner on 11/11/2017.
 */
public class MessagesBank {
    private ArrayList<Message> messages;

    MessagesBank() {
        messages = new ArrayList<>();
    }

    public synchronized void insertMessage() {

    }

    public synchronized Message getAndRemoveMessage(int generation) throws InterruptedException {
        Message correctGenerationMessage = findAndRemoveMsgFromGeneration(generation);
        while (correctGenerationMessage == null) {
            wait();
            correctGenerationMessage = findAndRemoveMsgFromGeneration(generation);
        }
        //At this point, we have a message with information that fits to the wanted generation
        return correctGenerationMessage;
    }

    private Message findAndRemoveMsgFromGeneration(int generation) {
        if (messages.isEmpty()) return null;
        Iterator<Message> it = messages.iterator();
        while (it.hasNext()) {
            Message iteratedMessage = it.next();
            if (iteratedMessage.generation == generation) {
                it.remove();
                return iteratedMessage;
            }
        }
        return null;
    }

    public synchronized void insertMessage(Message message) {
        messages.add(message);
        notifyAll(); //will awake the sleeping thread because a message is now inserted
    }
}
