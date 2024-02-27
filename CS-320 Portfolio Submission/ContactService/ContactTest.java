import org.junit.Test;
import static org.junit.Assert.*;


public class ContactTest {
    @Test
    public void testContactCreation() {
        // This test covers the valid creation of a Contact object
        Contact contact = new Contact("contact123", "Jon", "Thomas", "9015554321", "123 Main Street");

        assertEquals("contact123", contact.getContactId());
        assertEquals("Jon", contact.getFirstName());
        assertEquals("Thomas", contact.getLastName());
        assertEquals("9015554321", contact.getPhone());
        assertEquals("123 Main Street", contact.getAddress());
    }

    // Contact ID Tests
    @Test(expected = IllegalArgumentException.class)
    public void testNullContactId() {
        // This test covers the case where the contactId is null
        new Contact(null, "Jon", "Thomas", "9015554321", "123 Main Street");
    }
    @Test(expected = IllegalArgumentException.class)
    public void testContactIdLength() {
        // This test covers the case where the contactId is greater than 10 characters
        new Contact("aReallyLongContactId", "Jon", "Thomas", "9015554321", "123 Main Street");
    }

    // First Name Tests
    @Test(expected = IllegalArgumentException.class)
    public void testNullFirstName() {
        // This test covers the case where the firstName is null
        new Contact("contact123", null, "Thomas", "9015554321", "123 Main Street");
    }
    @Test(expected = IllegalArgumentException.class)
    public void testFirstNameLength() {
        // This test covers the case where the firstName is greater than 10 characters
        new Contact("contact123", "aReallyLongFirstName", "Thomas", "9015554321", "123 Main Street");
    }

    // Last Name Tests
    @Test(expected = IllegalArgumentException.class)
    public void testNullLastName() {
        // This test covers the case where the lastName is null
        new Contact("contact123", "Jon", null, "9015554321", "123 Main Street");
    }
    @Test(expected = IllegalArgumentException.class)
    public void testLastNameLength() {
        // This test covers the case where the lastName is greater than 10 characters
        new Contact("contact123", "Jon", "aReallyLongLastName", "9015554321", "123 Main Street");
    }

    // Phone Tests
    @Test(expected = IllegalArgumentException.class)
    public void testNullPhone() {
        // This test covers the case where the phone is null
        new Contact("contact123", "Jon", "Thomas", null, "123 Main Street");
    }
    @Test(expected = IllegalArgumentException.class)
    public void testPhoneLength() {
        // This test covers the case where the phone is not 10 characters
        new Contact("contact123", "Jon", "Thomas", "901555", "123 Main Street");
    }

    // Address Tests
    @Test(expected = IllegalArgumentException.class)
    public void testNullAddress() {
        // This test covers the case where the address is null
        new Contact("contact123", "Jon", "Thomas", "9015554321", null);
    }
    @Test(expected = IllegalArgumentException.class)
    public void testAddressLength() {
        // This test covers the case where the address is greater than 30 characters
        new Contact("contact123", "Jon", "Thomas", "9015554321", "123 Main Street, Apt 1, Memphis, TN 38103");
    }
}