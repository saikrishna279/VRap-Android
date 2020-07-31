# VRap

VRap is a college management app made for Velagapudi Ramakrishna Siddartha Engineering College, Vijayawada.

But unfortunately, couldn't be completed and is abandoned in it's infancy.

This project is open sourced so that any future gen students can continue this project or use this as a reference and make their own one.

# Features (Implemented/TODO)

 - Direct document access from Lecturers/Class Representatives.
 - Student Database
 - Advanced academic calendar to notify students of Curricular and Extra-Curricular Activities with Push Notifications.
 - Real-time Broadcasts from Administration and Staff.
 - In-App Updated
 - Student Profiling (Student Details, Achievements and all Records in one place!)
 - Automatic Wi-Fi Authentication (Authenticate the students automatically to log into the Wi-Fi)
 - Feel feel to add any more Features as you wish as it is all yours now :p

Many of the above mentioned features are not implemented.

# Sign Up Logic
```mermaid
graph TD
A[Registration Number] -->C[Submit details to Student CMS and Reset Password]
A --> H[Store in Firebase]
B --> I[Store in Firebase]
B[Mobile Number]-->C[Submit details to Student CMS and Reset Password]
C --> D[Get the changed CMS Password]
D --> G[Store in Firebase]
D-->E[Ask user for a new password, which will be used to authenticate the Firebase Account. This Password is used from now on]
E-->F[Create Firebase Account with Regd.No. and Password Provided]
```
# Login Logic
```mermaid
graph TD
A[Registration Number]
B[VRap Password]
C[Firebase Login]
A-->C
B-->C
C-->D[Get details]
```
# Implementations done till date
### User Authentication:
The User Sign Up and Log In Works as shown:
![](assets/login_gif.gif)
### Calendar Flow:
Calendar works as below:
![](assets/calendar-full_gif.gif) ![](assets/calendar_folding_gif.gif)
# OG:
### ECE:
 - Sai Krishna
 - Gnana Sai
 - Lubna
 - Madhurima
