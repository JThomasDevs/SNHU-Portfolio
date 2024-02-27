public class Contact {
    private final String contactId;  // This is final because it should not be updateable
    private String firstName;
    private String lastName;
    private String phone;
    private String address;

    public Contact(String contactId, String firstName, String lastName, String phone, String address) {
        if (contactId == null || contactId.length() > 10) {
            throw new IllegalArgumentException("Contact ID must be non-null and less than 10 characters");
        }
        this.contactId = contactId;
        // The setter methods handle validation so we can use them here to assign the rest of the variables their initial values
        setFirstName(firstName);
        setLastName(lastName);
        setPhone(phone);
        setAddress(address);
    }
    // Getters and setters

    // Contact ID
    public String getContactId() {
        return contactId;
    }
    // No setter for contactId as it is not updateable

    // First Name
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        if (firstName == null || firstName.length() > 10) {
            throw new IllegalArgumentException("First name must be non-null and less than 10 characters");
        }
        this.firstName = firstName;
    }
    // Last Name
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        if (lastName == null || lastName.length() > 10) {
            throw new IllegalArgumentException("Last name must be non-null and less than 10 characters");
        }
        this.lastName = lastName;
    }
    // Phone
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        if (phone == null || phone.length() != 10) {
            throw new IllegalArgumentException("Phone must be non-null and must contain exactly 10 digits");
        }
        this.phone = phone;
    }
    // Address
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        if (address == null || address.length() > 30) {
            throw new IllegalArgumentException("Address must be non-null and less than 30 characters");
        }
        this.address = address;
    }
}