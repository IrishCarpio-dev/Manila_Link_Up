# ManilaLinkUp

ManilaLinkUp is a mobile application that connects people within local communities in Manila through job postings. Users can post and find small tasks like babysitting, pet sitting, tutoring, and other short-term or long-term job opportunities.

We are currently studying Computer Science and this project was developed as a requirement for the **Mobile Applications and Development** subject.

---

##  Features

- User-friendly login and registration
- Post local job opportunities
- Browse available jobs
- View detailed job descriptions
- Clean and responsive UI design

---

##  Tech Stack

- Java
- Android Studio
- RecyclerView
- Material Design Components
- Firebase Auth & Firestore
- Laravel

---

## Project Architecture

- The application follows the Model-View-Adapter (MVA) pattern to ensure scalability:

- Activities & Fragments: Handles the lifecycle and UI logic (e.g., EmployerDashboard, ChatThreadActivity).

- Adapters: Custom complex adapters (e.g., ChatAdapter, EmployerAllRatingsAdapter) for dynamic data rendering.

- Models: Strongly-typed data classes (e.g., ChatModel, JobPostDashboardModel) ensuring data integrity.

- Resources: Highly customized XML layouts using RelativeLayout, LinearLayout and ConstraintLayout for pixel-perfect design.

---

## Developers

- Precious Irish Carpio (Project Lead)
- Rhovique Caling
- Loraine Gulo
- Dino Dumanon

---

## Purpose

In the bustling environment of Manila, finding trustworthy local help or quick income opportunities can be difficult. ManilaLinkUp simplifies this by leveraging mobile technology to foster community trust and economic opportunity at the neighborhood level.
