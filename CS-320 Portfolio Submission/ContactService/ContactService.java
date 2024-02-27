import java.util.HashMap;

public class ContactService {
    private final HashMap<String, Contact> contacts;

    public ContactService() {
        this.contacts = new HashMap<>();
    }

    public void addContact(Contact contact) {
        String contactId = contact.getContactId();
        // Check if contact with contactId already exists
        if (contacts.containsKey(contactId)) {
            throw new IllegalArgumentException("Contact with ID " + contactId + " already exists");
        }
        contacts.put(contactId, contact);
    }

    public void deleteContact(String contactId) {
        // If no such contactId exists, this will return null and the HashMap will remain unchanged
        contacts.remove(contactId);
    }

    public void updateContactField(String contactId, String fieldName, String value) {
        Contact contact = contacts.get(contactId);  // This will return null if no such contactId exists

        if (contact == null) {
            throw new IllegalArgumentException("Contact with ID " + contactId + " does not exist");
        }
        // Switch on fieldName to determine which setter method to call
        switch (fieldName) {
            case "firstName":
                contact.setFirstName(value);
                break;
            case "lastName":
                contact.setLastName(value);
                break;
            case "phone":
                contact.setPhone(value);
                break;
            case "address":
                contact.setAddress(value);
                break;
            default:
                throw new IllegalArgumentException("Invalid field name: " + fieldName);
        }
    }

    public Contact getContact(String contactId) {
        // This method is used in the test cases to verify that the contact was added and will return null if no such contactId exists
        return contacts.get(contactId);
    }
}
