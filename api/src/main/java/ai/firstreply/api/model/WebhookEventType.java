package ai.firstreply.api.model;

/**
 * Event names a webhook can subscribe to ({@link WebhookCreate#getEvent()}) and that arrive
 * as {@link WebhookEvent#getEvent()}.
 */
public interface WebhookEventType {
    String CONTACT_CREATE = "contact.create";
    String CONTACT_UPDATE = "contact.update";
    String CONTACT_DELETE = "contact.delete";

    String CONVERSATION_CREATE = "conversation.create";
    String CONVERSATION_UPDATE = "conversation.update";
    String CONVERSATION_DELETE = "conversation.delete";
    String CONVERSATION_REASSIGN = "conversation.reassign";

    String MESSAGE_CREATE = "message.create";
    String MESSAGE_UPDATE = "message.update";
    String MESSAGE_DELETE = "message.delete";

    String CONTACT_FIELD_CREATE = "contact_field.create";
    String CONTACT_FIELD_UPDATE = "contact_field.update";
    String CONTACT_FIELD_DELETE = "contact_field.delete";

    String CONVERSATION_FILTER_CREATE = "conversation_filter.create";
    String CONVERSATION_FILTER_UPDATE = "conversation_filter.update";
    String CONVERSATION_FILTER_DELETE = "conversation_filter.delete";

    String KNOWLEDGE_CREATE = "knowledge.create";
    String KNOWLEDGE_UPDATE = "knowledge.update";
    String KNOWLEDGE_DELETE = "knowledge.delete";

    String KNOWLEDGE_SOURCE_CREATE = "knowledge_source.create";
    String KNOWLEDGE_SOURCE_UPDATE = "knowledge_source.update";
    String KNOWLEDGE_SOURCE_DELETE = "knowledge_source.delete";

    String CLASSIFICATION_CREATE = "classification.create";
    String CLASSIFICATION_UPDATE = "classification.update";
    String CLASSIFICATION_DELETE = "classification.delete";

    String TEMPLATE_CREATE = "template.create";
    String TEMPLATE_UPDATE = "template.update";
    String TEMPLATE_DELETE = "template.delete";

    String PROMPT_CREATE = "prompt.create";
    String PROMPT_UPDATE = "prompt.update";
    String PROMPT_DELETE = "prompt.delete";

    String INBOX_CREATE = "inbox.create";
    String INBOX_UPDATE = "inbox.update";
    String INBOX_DELETE = "inbox.delete";

    String POST_CREATE = "post.create";
    /** Carries the post's status, so published and failed posts arrive as updates. */
    String POST_UPDATE = "post.update";
    String POST_DELETE = "post.delete";

    String ORGANIZATION_UPDATE = "organization.update";

    String MEMBER_ADD = "member.add";
    String MEMBER_REMOVE = "member.remove";

    /** Sent by {@code testWebhook}. */
    String WEBHOOK_TEST = "webhook.test";
}
