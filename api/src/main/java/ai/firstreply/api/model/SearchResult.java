package ai.firstreply.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchResult {
    private String query;
    private List<Conversation> conversations;
    private int conversationCount;
    private List<Contact> contacts;
    private int contactCount;
    private List<KnowledgeEntry> knowledge;
    private int knowledgeCount;
    private List<Message> messages;
    private int messageCount;
}
