
# OAuth2 Demo — Google & GitHub login (Step-by-step)

This Spring Boot app demonstrates OAuth2 login with Google and GitHub, automatic user provisioning on first login, and a minimal profile page where users can view and edit their display name and bio.

This README explains exactly what to configure, how to run locally, and how to troubleshoot two common issues you may see:

- 404 when clicking "Login with GitHub" (usually means the registration id/path isn't found)
- 400 from Google (usually redirect_uri_mismatch or invalid client)

Contents
- Provider setup (Google & GitHub)
- What to put in the app (properties or environment variables)
- Run the app locally
- Tests
- Troubleshooting (404 / 400)

---

1) Provider setup (create credentials)

A) Google — create OAuth client
1. Go to Google Cloud Console -> APIs & Services -> Credentials.
2. Create an OAuth 2.0 Client ID (if required, configure the OAuth consent screen first).
3. When asked for an Authorized redirect URI, add:

   http://localhost:8080/login/oauth2/code/google

4. Copy the Client ID and Client Secret.

B) GitHub — create OAuth App
1. Go to GitHub -> Settings -> Developer settings -> OAuth Apps -> New OAuth App.
2. Fill in the details. For the Authorization callback URL enter:

   http://localhost:8080/login/oauth2/code/github

3. Save and copy the Client ID and Client Secret.

If your app runs on a different host/port or uses a context-path, replace `http://localhost:8080` with your actual base URL.

---

2) What to put in the app

You can either put credentials in `src/main/resources/application.properties` for local testing, or set environment variables (recommended).

A) Add to `src/main/resources/application.properties` (quick local dev):

```properties
# Google
spring.security.oauth2.client.registration.google.client-id=YOUR_GOOGLE_CLIENT_ID
spring.security.oauth2.client.registration.google.client-secret=YOUR_GOOGLE_CLIENT_SECRET

# GitHub
spring.security.oauth2.client.registration.github.client-id=YOUR_GITHUB_CLIENT_ID
spring.security.oauth2.client.registration.github.client-secret=YOUR_GITHUB_CLIENT_SECRET
spring.security.oauth2.client.registration.github.scope=user:email,read:user

# H2 dev DB
spring.datasource.url=jdbc:h2:mem:testdb
spring.h2.console.enabled=true
