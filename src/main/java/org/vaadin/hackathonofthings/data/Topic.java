package org.vaadin.hackathonofthings.data;
/**
 * Topic for a data type.
 */
public class Topic {

    private final String topicId;

    public Topic(String topicId) {
        this.topicId = topicId;
    }

    public String getId() {
        return this.topicId;
    }

}