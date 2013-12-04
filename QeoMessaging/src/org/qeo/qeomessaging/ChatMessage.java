/**************************************************************
 ********          THIS IS A GENERATED FILE         ***********
 **************************************************************/

package org.qeo.qeomessaging;

/**
 * A simple chat message.
 */
public class ChatMessage
{
    /**
     * The user sending the message.
     */
    public String from;

    /**
     * The message.
     */
    public String message;

    public ChatMessage()
    {
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == this) {
            return true;
        }
        if ((obj == null) || (obj.getClass() != this.getClass())) {
            return false;
        }
        final ChatMessage myObj = (ChatMessage)obj;
        if (!from.equals(myObj.from)) {
            return false;
        }
        if (!message.equals(myObj.message)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((from == null) ? 0 : from.hashCode());
        result = prime * result + ((message == null) ? 0 : message.hashCode());
        return result;
    }
}
