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

    /* This method tries to get a message from the messages array with the wanted generation.
    * If there is no message that fits (or if the array is empty), the thread waits. */
    synchronized Message getAndRemoveMessage(int generation) {
        Message correctGenerationMessage = findAndRemoveMsgFromGeneration(generation);
        while (correctGenerationMessage == null) {
            try {
                System.out.println("Thread wait()"); //TODO: delete
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            correctGenerationMessage = findAndRemoveMsgFromGeneration(generation);
        }
        //At this point, we have a message with information that fits to the wanted generation
        System.out.println("Thread wake up"); //TODO: delete
        return correctGenerationMessage;
    }

    private Message findAndRemoveMsgFromGeneration(int generation) {
        if (messages.isEmpty()) return null;
        Iterator<Message> it = messages.iterator();
        while (it.hasNext()) {
            Message iteratedMessage = it.next();
            if (iteratedMessage.getGeneration() == generation) {
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
