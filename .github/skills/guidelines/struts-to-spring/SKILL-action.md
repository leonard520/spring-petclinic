# Struts to Spring Boot - Action Conversion Skills

Skills for converting Struts Action classes to Spring MVC Controllers.

---

## Skill: convert-action-to-controller

**Description:** Convert Struts Action classes to Spring MVC Controllers.

**Trigger:** When file contains `extends ActionSupport` or imports `com.opensymphony.xwork2.ActionSupport`.

**Input:** Java file with Struts Action class.

**Steps:**

1. **Remove imports:**
```java
// DELETE
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.ActionSupport;
```

2. **Add imports:**
```java
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.ui.Model;
```

3. **Transform class declaration:**
```java
// FROM
public class HelloAction extends ActionSupport { }

// TO
@Controller
public class HelloController { }
```

4. **Transform execute() method:**
```java
// FROM
public String execute() {
    return SUCCESS;
}

// TO
@GetMapping("/hello")  // derive path from class name: HelloAction → /hello
public String execute(Model model) {
    return "success";
}
```

5. **URL mapping derivation rules:**
   - Remove `Action` suffix from class name
   - Convert to lowercase
   - Example: `HelloWorldAction` → `/helloworld`

6. **HTTP method selection:**
   - Default: `@GetMapping`
   - Use `@PostMapping` if method contains form processing or data modification

**Transformation Table - Return Constants:**

| Struts | Spring |
|--------|--------|
| `SUCCESS` | `"success"` |
| `ERROR` | `"error"` |
| `INPUT` | `"input"` |
| `Action.SUCCESS` | `"success"` |
| `com.opensymphony.xwork2.Action.SUCCESS` | `"success"` |

**Output:** Spring MVC Controller class.

---

## Skill: convert-action-properties

**Description:** Convert Struts Action properties (with getters/setters) to Spring request parameters.

**Trigger:** When Action class has private fields with getter/setter pairs used in execute().

**Input:** Action class with properties.

**Transformation Rules:**

1. **Simple properties → @RequestParam:**
```java
// FROM (Struts)
public class LoginAction extends ActionSupport {
    private String username;
    private String password;
    
    public void setUsername(String u) { this.username = u; }
    public String getUsername() { return username; }
    // ... setters/getters
    
    public String execute() {
        // uses username, password
        return SUCCESS;
    }
}

// TO (Spring)
@Controller
public class LoginController {
    @PostMapping("/login")
    public String login(
            @RequestParam String username,
            @RequestParam String password,
            Model model) {
        // uses username, password
        return "success";
    }
}
```

2. **Complex objects → @ModelAttribute:**
```java
// TO (Spring) - for multiple related fields
@PostMapping("/login")
public String login(@ModelAttribute LoginForm form, Model model) {
    // form.getUsername(), form.getPassword()
    return "success";
}
```

3. **Output properties → Model attributes:**
```java
// FROM: Action property used in JSP
private String message;
public String getMessage() { return message; }

// TO: Add to Model
model.addAttribute("message", message);
```

**Output:** Controller method with proper parameter annotations.
