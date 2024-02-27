import org.junit.Test;
import static org.junit.Assert.*;

public class ContactServiceTest {
    @Test
    public void testAddContact() {
        // This test covers the valid addition of a Contact object
        ContactService contactService = new ContactService();
        Contact contact = new Contact("1", "Jon", "Thomas", "9015554321", "123 Main Street");

        contactService.addContact(contact);

        assertEquals(contact, contactService.getContact("1"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddDuplicateContactId() {
        // This test covers the case where a contact with a duplicate contactId is attempted to be added
        ContactService contactService = new ContactService();
        Contact contact1 = new Contact("1", "Jon", "Thomas", "9015554321", "123 Main Street");
        Contact contact2 = new Contact("1", "Adam", "West", "3216754321", "475 East Street");

        contactService.addContact(contact1);
        contactService.addContact(contact2);  // The exception is thrown here
    }

    @Test
    public void testDeleteContact() {
        // This test covers the valid deletion of a Contact object
        ContactService contactService = new ContactService();
        Contact contact = new Contact("1", "Jon", "Thomas", "9015554321", "123 Main Street");

        contactService.addContact(contact);
        contactService.deleteContact("1");

        assertNull(contactService.getContact("1"));
    }

    @Test
    public void testUpdateContactField() {
        // This test covers the valid update of several fields in a Contact object
        ContactService contactService = new ContactService();
        Contact contact = new Contact("1", "Jon", "Thomas", "9015554321", "123 Main Street");

        contactService.addContact(contact);

        contactService.updateContactField("1", "firstName", "Adam");
        contactService.updateContactField("1", "lastName", "West");
        contactService.updateContactField("1", "phone", "3216754321");
        contactService.updateContactField("1", "address", "475 East Street");

        assertEquals("Adam", contactService.getContact("1").getFirstName());
        assertEquals("West", contactService.getContact("1").getLastName());
        assertEquals("3216754321", contactService.getContact("1").getPhone());
        assertEquals("475 East Street", contactService.getContact("1").getAddress());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateInvalidContactField() {
        // This test covers the case where an invalid field name is passed to updateContactField
        ContactService contactService = new ContactService();
        Contact contact = new Contact("1", "Jon", "Thomas", "9015554321", "123 Main Street");

        contactService.addContact(contact);

        contactService.updateContactField("1", "invalidFieldName", "Adam");  // The exception is thrown here
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateNonExistantContact() {
        // This test covers the case where a contactId is passed to updateContactField that does not exist
        ContactService contactService = new ContactService();

        contactService.updateContactField("1", "firstName", "Adam");  // The exception is thrown here
    }

    // Additional tests to test null returns from the get and delete methods
    @Test
    public void testGetNonExistantContact() {
        // This test covers the case where a contactId is passed to getContact that does not exist
        ContactService contactService = new ContactService();

        assertNull(contactService.getContact("1"));
    }

    @Test
    public void testDeleteNonExistantContact() {
        // This test covers the case where a contactId is passed to deleteContact that does not exist
        ContactService contactService = new ContactService();

        contactService.deleteContact("1");
    }
}