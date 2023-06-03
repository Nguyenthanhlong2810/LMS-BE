package ntlong.utils;

public enum AccountEnum {
    Admin("admin", "admin", "admin@gmail.com.vn","ROLE_ADMIN"),
    Admin2("admin2", "admin2", "admin2@gmail.com.vn","ROLE_ADMIN"),
    Client("client", "client", "client@gmail.com.vn","ROLE_CLIENT");

    AccountEnum(String username, String password, String email, String role) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    private String username;
    private String password;
    private String email;
    private String role;
}
