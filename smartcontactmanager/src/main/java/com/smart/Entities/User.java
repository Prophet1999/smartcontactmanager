package com.smart.Entities;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name="USER",uniqueConstraints = @UniqueConstraint(columnNames = {"uid", "email"}))
public class User {
	
	@AssertTrue(message="User is deactivated")
	@NotNull(message="Activation state not mentioned")
	@Column(columnDefinition="BOOLEAN")
	private boolean enabled=true;
	
	@Pattern(regexp="^(ROLE_USER|ROLE_ADMIN)$",message="Only 'USER' and 'ADMIN' are allowed")
	@Column(columnDefinition = "varchar(10) default 'ROLE_USER'")
	private String role;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int uid;
	
	@NotBlank(message="Name cannot be blank")
	@Size(min=3,max=25,message="Min 3 and Max 25 characters are allowed")
	@Pattern(regexp = "^[a-zA-Z0-9 ]+$",
    message = "Name should contain only uppercase, lowercase characters and digits with no special characters")
	private String name;
	
	@NotBlank(message="Email cannot be empty")
	@Pattern(regexp="\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}\\b"
	,message="Invalid Email")
	@Size(min=9,max=40,message="Min 9 and Max 40 characters are allowed")
	@Column(unique=true)
	private String email;
	
	//@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*])[a-zA-Z0-9!@#$%^&*]+$",
    //message = "Password must contain atleast 1 uppercase, 1 lowercase, 1 special character and 1 digit ")
	@Size(min=6,max=100,message="Min 6 and Max 20 characters are allowed")
	private String password;
	
	private String imageUrl;
	
	@Size(max=250,message="Max 250 characters are allowed")
	private String about;
	
	@OneToMany(cascade=CascadeType.ALL,fetch=FetchType.LAZY,mappedBy="user")
	private List<Contact>contacts=new ArrayList<>();
	
	public User() {
		super();
		// TODO Auto-generated constructor stub
	}

	public User(boolean enabled,
			@Pattern(regexp = "^(USER|ADMIN)$", message = "Only 'USER' and 'ADMIN' are allowed") String role, int uid,
			String name, @Email String email, String password, String imageUrl, String about, List<Contact> contacts) {
		super();
		this.enabled = enabled;
		this.role = role;
		this.uid = uid;
		this.name = name;
		this.email = email;
		this.password = password;
		this.imageUrl = imageUrl;
		this.about = about;
		this.contacts = contacts;
	}

	@Override
	public String toString() {
		return "User [enabled=" + enabled + ", role=" + role + ", uid=" + uid + ", name=" + name + ", email=" + email
				+ ", password=" + password + ", imageUrl=" + imageUrl + ", about=" + about + ", contacts=" + contacts
				+ "]";
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getAbout() {
		return about;
	}

	public void setAbout(String about) {
		this.about = about;
	}

	public List<Contact> getContacts() {
		return contacts;
	}

	public void setContacts(List<Contact> contacts) {
		this.contacts = contacts;
	}
}
