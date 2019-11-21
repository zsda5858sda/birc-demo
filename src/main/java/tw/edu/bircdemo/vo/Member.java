package tw.edu.bircdemo.vo;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "member")
public class Member {
    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String gender;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "first_name", length = 20, nullable = false)
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Basic
    @Column(name = "last_name", length = 20, nullable = false)
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Basic
    @Column(name = "email", length = 100, nullable = false)
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Basic
    @Column(name = "gender", length = 1, nullable = false)
    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Member member = (Member) o;
        return id == member.id &&
                Objects.equals(firstName, member.firstName) &&
                Objects.equals(lastName, member.lastName) &&
                Objects.equals(email, member.email) &&
                Objects.equals(gender, member.gender);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, email, gender);
    }
}
