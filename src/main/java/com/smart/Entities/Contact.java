package com.smart.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name="CONTACT",uniqueConstraints = @UniqueConstraint(columnNames = {"phone", "cid","email"}))
public class Contact {
	@Size(min=10,max=10,message="Must be 10 digits")
	@NotBlank(message="Phone cannot be empty")
	@Column(unique=true)
	private String phone;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int cid;
	
	@Size(max=1000,message="Max 1000 characters are allowed")
	private String description;
	
	@NotBlank(message="Name cannot be blank")
	@Size(min=3,max=25,message="Min 3 and Max 25 characters are allowed")
	@Pattern(regexp = "^[a-zA-Z0-9 ]+$",
    message = "Name should contain only uppercase, lowercase characters and digits with no special characters")
	private String name;
	
	private String nickName;
	
	@NotBlank(message="Work cannot be blank")
	private String work;
	
	@NotBlank(message="Email cannot be blank")
	@Pattern(regexp="\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}\\b"
	,message="Invalid Email")
	@Column(unique=true)
	private String email;
	
	private String image;
	
	@ManyToOne
	@JsonIgnore
	private User user;

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public int getCid() {
		return cid;
	}

	public void setCid(int cid) {
		this.cid = cid;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getWork() {
		return work;
	}

	public void setWork(String work) {
		this.work = work;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public String toString() {
		return "Contact [phone=" + phone + ", cid=" + cid + ", description=" + description + ", name=" + name
				+ ", nickName=" + nickName + ", work=" + work + ", email=" + email + ", image=" + image + ", user="
				+ user + "]";
	}

	public Contact(String phone, int cid, String description, String name, String nickName, String work,
			@Email String email, String image, User user) {
		super();
		this.phone = phone;
		this.cid = cid;
		this.description = description;
		this.name = name;
		this.nickName = nickName;
		this.work = work;
		this.email = email;
		this.image = image;
		this.user = user;
	}

	public Contact() {
		super();
		// TODO Auto-generated constructor stub
	}
}
