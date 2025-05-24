# TeacherAgenda User Guide

## 1. Introduction

### Overview
TeacherAgenda is a desktop application designed to help teachers organize their schedule, tasks, teaching materials, and other relevant information. It provides a centralized platform for managing events, to-do lists, locations, activities (including simple diagrams and pseudocode), teaching resources, quick notes, and more.

### System Requirements
*   **Java:** Version 11 or higher.
*   **Database:** Uses an embedded H2 database. Data is stored locally in a file named `teacheragenda.mv.db` (or similar) in the directory from which the application is run.
*   **Operating System for Notifications:** OS-level (desktop) notifications are supported. Email notifications require configuration.
*   **Internet Connection:** Required for fetching map tiles in the "Locations" tab.

## 2. Getting Started

### Running the Application
*   **From a packaged JAR:** If you have a `TeacherAgenda.jar` file (or similar), you can typically run it by double-clicking or using the command: `java -jar TeacherAgenda.jar` in your terminal/command prompt. Ensure Java is correctly installed and configured in your system's PATH.
*   **From an IDE (Integrated Development Environment):** If you have the source code, you can run the `com.teacheragenda.MainApp` class directly from your IDE (e.g., IntelliJ IDEA, Eclipse).

### Main Window Layout
The application uses a tabbed interface for easy navigation between different sections:
*   Calendar
*   Tasks
*   Locations
*   Activities
*   Materials
*   Quick Notes
*   Statistics
*   Tools

Most tabs follow a pattern where a list or table of items is on one side, and a form for adding/editing details is on the other.

### Email Configuration
For email notifications (e.g., for event/task reminders) to work, you **must** configure your email server (SMTP) details in the `application.properties` file. This file is typically located in the `src/main/resources` directory if running from source, or alongside the JAR file if packaged appropriately (or within it).

You need to set the following properties:
```properties
# Spring Mail Properties (User needs to configure these)
spring.mail.host=your.smtp.server.com
spring.mail.port=587 # Or your SMTP port, e.g., 465 for SSL
spring.mail.username=your_email_address@example.com
spring.mail.password=your_email_password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true # Or false if using SSL on port 465
spring.mail.from=your_email_address@example.com

# Application Specific Properties
# This is the email address where TeacherAgenda will send notifications TO.
teacher.agenda.notification.email=your_teacher_email_address@example.com
```
**Important:** Replace placeholder values with your actual SMTP server details and email credentials. The `teacher.agenda.notification.email` is the address the application will send reminders *to*.

## 3. Core Features (Tab by Tab)

### Calendar Tab
*   **Viewing Events:** The main area displays a `MonthView` calendar.
*   **Navigating Months:** Use the "**< Prev Month**" and "**Next Month >**" buttons to change the displayed month. Click "**Today**" to jump to the current month and select today's date. The current month and year are displayed at the top.
*   **Event Markers:** Days with one or more events are visually marked in the `MonthView`. Hovering over such a day will show a tooltip indicating the number of events.
*   **Viewing Events for a Date:** Click on a date in the `MonthView`. Any events scheduled for that day will appear in the list below the calendar titled "Events for Selected Date:".
*   **Adding an Event:**
    1.  Select the desired start date in the `MonthView` (this pre-fills the date fields in the form).
    2.  Fill in the "Title", "Start Time (HH:MM)", "End Date", "End Time (HH:MM)", "Location" (select from dropdown), and "Description" in the "Manage Events" form on the right.
    3.  Click "**Add Event**".
*   **Editing an Event:**
    1.  Select a date in `MonthView`.
    2.  Select the event from the "Events for Selected Date:" list. The form will populate with its details.
    3.  Modify the details in the form.
    4.  Click "**Update Event**".
*   **Deleting an Event:**
    1.  Select a date in `MonthView`.
    2.  Select the event from the "Events for Selected Date:" list.
    3.  Click "**Delete Event**". A confirmation dialog will appear.
*   **Clearing the Form:** Click "**Clear**" to reset the form fields. If a date is selected on the calendar, the date fields will remain pre-filled for that date.
*   **Exporting Events to iCalendar (.ics):**
    *   **Purpose:** To share your events with other calendar applications (like Google Calendar, Outlook Calendar, Apple Calendar) or to create a backup of your event schedule.
    *   **How to use:**
        1.  In the Calendar tab, click the "**Export All Events (.ics)**" button located in the top toolbar.
        2.  A file dialog will appear, allowing you to choose a location and name for your export file (defaulting to `TeacherAgendaEvents.ics`).
        3.  Click "Save".
    *   **Result:** All events currently stored in TeacherAgenda will be exported into the selected `.ics` file.
*   **Importing Events from iCalendar (.ics):**
    *   **Purpose:** To add events from other calendar applications or from `.ics` files into TeacherAgenda.
    *   **How to use:**
        1.  In the Calendar tab, click the "**Import Events (.ics)**" button located in the top toolbar.
        2.  A file dialog will appear. Select the `.ics` or `.ical` file you wish to import.
        3.  Click "Open".
    *   **Result:**
        *   Events from the file will be parsed and added to your TeacherAgenda calendar.
        *   The application will show a confirmation message indicating how many events were successfully imported and saved.
        *   **Duplicate Events:** Please note that the current import process does not check for duplicate events. If you import a file containing events already in your TeacherAgenda, they will be imported again, creating duplicates.
        *   **Timezones:** Event times from the `.ics` file are generally interpreted and converted to your system's default timezone.

### Tasks Tab
*   **Viewing Tasks:** Tasks are displayed in a table with columns: Description, Due Date, Priority, Completed (checkbox), Creation Date. The table is sorted by Priority (High first), then by your custom Sort Order, then by Due Date.
*   **Adding a Task:**
    1.  Fill in "Description", "Due Date" (optional), and "Priority" in the "Manage Task" form.
    2.  Click "**Add Task**".
*   **Editing a Task:**
    1.  Select a task from the table. Its details will appear in the form.
    2.  Modify the details.
    3.  Click "**Update Task**".
*   **Deleting a Task:** Select a task and click "**Delete Task**". Confirm the deletion.
*   **Marking Tasks Complete:**
    *   Check the "Completed" checkbox directly in the table row for the task.
    *   Or, select a task and click the "**Toggle Complete**" button.
*   **Task Priority Colors:** The text color in the "Priority" column indicates its level:
    *   **HIGH:** Red
    *   **MEDIUM:** Orange
    *   **LOW:** Green (or Black, depending on theme)
*   **Drag-and-Drop Reordering:**
    *   You can manually reorder tasks within the table by clicking and dragging a task row to a new position.
    *   This changes the task's `sortOrder` relative to other tasks, primarily affecting its order *within the same priority group*. The table will refresh to show the new persisted order.
    *   Tooltip on table: "Drag and drop tasks to reorder them within their priority group."

### Locations Tab
*   **Viewing Locations:**
    *   The tab is split. On the left is an interactive **MapView** showing location markers. On the right is a **Location List** (table).
*   **Adding a Location:**
    1.  Fill in "Name", "Address", "Latitude", "Longitude", and "Description" in the "Manage Location" form.
        *   Latitude and Longitude are decimal degrees (e.g., 40.7128, -74.0060). Tooltips provide examples.
    2.  Click "**Add Location**". The location will appear in the table and as a marker on the map (if coordinates are valid).
*   **Editing a Location:** Select a location from the table, modify its details (including coordinates) in the form, and click "**Update Location**".
*   **Deleting a Location:** Select a location and click "**Delete Location**". Confirm the deletion.
*   **MapView Interaction:**
    *   **Markers:** Locations with valid coordinates are shown as blue markers on the map. Hovering (or clicking) shows the location name.
    *   **Panning/Zooming:** Use your mouse to drag (pan) and scroll (zoom) the map.
    *   **Synchronization:**
        *   Clicking a marker on the map selects the corresponding location in the table and centers the map on it.
        *   Selecting a location in the table centers the map on it (if coordinates exist) and zooms in.
*   **Refreshing:** Click "**Refresh Locations**" to reload data into the table and update map markers.

### Activities Tab
This tab allows you to plan detailed activities, link them to events, tasks, and locations, and even create simple diagrams or pseudocode.
*   **Layout:** The activity list is on the left. The center area has three sub-tabs: "Details & Tasks", "Diagram", and "Pseudocode".
*   **Viewing Activities:** Select an activity from the list on the left to view its details, diagram, and pseudocode in the respective sub-tabs.
*   **Adding/Saving an Activity:**
    1.  To create a new activity, click "**New/Clear Form**" in the "Details & Tasks" sub-tab.
    2.  Fill in the activity "Name", "Date", "Time (HH:MM)", "Duration (min)", and "Description".
    3.  Optionally, link to an existing "Event" and "Location" using the dropdowns.
    4.  Assign tasks using the "Available Tasks" and "Selected Tasks" list views (use ">>" and "<<" buttons).
    5.  Switch to the "Diagram" tab to create a visual plan (see below).
    6.  Switch to the "Pseudocode" tab to write procedural steps (see below).
    7.  Click "**Add/Save Activity Details**" (in "Details & Tasks" tab) to save all aspects of the activity. This button saves the main details, linked items, and any captured diagram XML or pseudocode.
*   **Editing an Activity:** Select an activity from the list. Its details, diagram, and pseudocode will load. Make changes in any tab and click "**Add/Save Activity Details**" to save.
*   **Deleting an Activity:** Select an activity from the list and click "**Delete Selected Activity**". Confirm the deletion.
*   **Diagramming Sub-Tab:**
    *   A simple diagram editor is provided.
    *   **Toolbar:**
        *   "**Add Box**": Adds a rectangular shape.
        *   "**Add Circle**": Adds an elliptical shape.
        *   "**Add Edge**": Select two shapes, then click this to connect them with an edge.
        *   "**Save Diagram**": Captures the current diagram state as XML data associated with the selected activity *in memory*. You still need to click "Add/Save Activity Details" to persist this to the database.
        *   "**Load Diagram**": Loads the saved diagram for the currently selected activity. This happens automatically when you select an activity.
*   **Pseudocode Sub-Tab:**
    *   A simple text area for writing pseudocode or procedural steps.
    *   "**Save Pseudocode**": Captures the current text *in memory*. Click "Add/Save Activity Details" to persist.
    *   "**Load Pseudocode**": Loads the saved pseudocode for the selected activity. This happens automatically when you select an activity.

### Materials Tab
Manage metadata for your teaching resources and link to local files.
*   **Viewing Materials:** Materials are listed in a table showing Name, Type, Category, Description, Upload Date, and File Path.
*   **Adding Material Metadata:**
    1.  Fill in "Name", "Category", and "Description".
    2.  Click "**Browse...**" to use a file chooser to select the material file. Its path will be stored.
    3.  Click "**Add Material**".
*   **Editing Material Metadata:** Select a material, edit its details (you can also change the file path by browsing again), and click "**Update Info**".
*   **Deleting Material Metadata:** Select a material and click "**Delete Info\***".
    *   **Important Note:** This only deletes the *record* of the material from the TeacherAgenda application. **The actual file on your computer is NOT deleted.**
*   **Opening a Material File:** Select a material and click "**Open Material**". The application will attempt to open the linked file using your system's default program for that file type.
*   **File Type Display:** The "Type" column provides a textual hint about the file type (e.g., "[PDF]", "[DOC]", "[IMG]").

### Quick Notes Tab
For jotting down quick thoughts and reminders.
*   **Layout:** A list of notes on the left, and a text area for content on the right.
*   **Viewing Notes:** Click a note in the list to view its content in the text area. The list shows a preview and the last modified timestamp.
*   **Adding a New Note:**
    1.  Click "**New/Clear**". The text area clears and gains focus.
    2.  Type your note.
    3.  Click "**Save Note**".
*   **Editing a Note:**
    1.  Select a note from the list. Its content appears and the text area gains focus.
    2.  Edit the text.
    3.  Click "**Save Note**".
*   **Deleting a Note:**
    *   Select a note and click "**Delete Note**".
    *   Or, right-click a note in the list and choose "Delete Note" from the context menu.
*   **Timestamps:** The "Created" and "Modified" timestamps for the selected note are displayed below the text area.

### Statistics Tab
View basic statistics about your tasks.
*   **Task Counts:** Displays "Total Tasks", "Completed Tasks", and "Pending Tasks".
*   **Task Status PieChart:** A visual breakdown of completed vs. pending tasks, including counts and percentages on slices.
*   **Tasks by Priority BarChart:** Shows the number of tasks for each priority level (High, Medium, Low). This chart is hidden if no tasks have priorities assigned or if there are no tasks at all.
*   **Refreshing:** Click "**Refresh Statistics**" to update the displayed data. The button is briefly disabled during refresh.

### Tools Tab
*   **Send Reminders Now:**
    *   Click this button to manually trigger the sending of reminders.
    *   **Desktop Notifications:** OS-level (toast) notifications will appear for upcoming events (within 24 hours) and tasks due today or overdue.
    *   **Email Notifications:** If you have configured your email settings in `application.properties`, emails for these same reminders will also be sent to the `teacher.agenda.notification.email`.
    *   A summary dialog will inform you of how many notifications were processed.

## 4. Non-Functional Aspects

*   **Tooltips:** Many buttons and some fields have tooltips. Hover your mouse over them for a brief explanation of their function.
*   **Keyboard Navigation & Focus:** Efforts have been made to improve keyboard navigation. For example, after clearing a form or selecting an item, focus is often moved to the most logical field for quick data entry.

## 5. Troubleshooting/FAQ

*   **Email notifications not working?**
    *   Ensure you have correctly configured all `spring.mail.*` properties and `teacher.agenda.notification.email` in your `application.properties` file.
    *   Check that the details (SMTP server, port, username, password) are accurate and that your email server is accessible.
    *   Look for error messages in the application's console/log output if running from an IDE or terminal.
*   **Map not showing (Locations Tab)?**
    *   Ensure you have an active internet connection, as map tiles are typically fetched from online sources (e.g., OpenStreetMap).
    *   If you have entered coordinates for a location but it doesn't appear where expected, double-check the latitude and longitude values for accuracy and correct sign (e.g., negative for West longitude/South latitude).
*   **Diagram features seem basic?**
    *   The diagramming tool is for simple visual planning. For very complex diagrams, dedicated external tools might be more suitable.
*   **File not opening from Materials tab?**
    *   Ensure the file path stored is still correct and the file exists at that location.
    *   Make sure your operating system has a default application configured to open that file type.

---
Thank you for using TeacherAgenda!
