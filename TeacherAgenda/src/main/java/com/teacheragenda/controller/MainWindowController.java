package com.teacheragenda.controller;

import com.teacheragenda.model.Activity;
import com.teacheragenda.model.Event;
import com.teacheragenda.model.Location;
import com.teacheragenda.model.Priority;
import com.teacheragenda.model.QuickNote;
import com.teacheragenda.model.Task;
import com.teacheragenda.model.TeachingMaterial;
import com.teacheragenda.service.ActivityService;
import com.teacheragenda.service.EventService;
import com.teacheragenda.service.LocationService;
import com.teacheragenda.service.NotificationService;
import com.teacheragenda.service.QuickNoteService;
import com.teacheragenda.service.TaskService;
import com.teacheragenda.service.TeachingMaterialService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class MainWindowController {

    //<editor-fold desc="FXML Fields - Calendar Tab">
    @FXML private Label currentMonthYearLabel;
    @FXML private ListView<Event> eventListView;
    @FXML private TextField eventTitleField;
    @FXML private DatePicker eventStartDatePicker;
    @FXML private TextField eventStartTimeField;
    @FXML private DatePicker eventEndDatePicker;
    @FXML private TextField eventEndTimeField;
    @FXML private ComboBox<Location> eventLocationComboBox;
    @FXML private TextArea eventDescriptionArea;
    @FXML private Button addEventButton;
    @FXML private Button updateEventButton;
    @FXML private Button deleteEventButton;
    @FXML private Button clearEventFormButton;
    //</editor-fold>

    //<editor-fold desc="FXML Fields - Tasks Tab">
    @FXML private TableView<Task> taskTableView;
    @FXML private TableColumn<Task, String> taskDescriptionColumn;
    @FXML private TableColumn<Task, LocalDate> taskDueDateColumn;
    @FXML private TableColumn<Task, Priority> taskPriorityColumn;
    @FXML private TableColumn<Task, Boolean> taskCompletedColumn;
    @FXML private TableColumn<Task, LocalDateTime> taskCreationDateColumn;
    @FXML private TextField taskDescriptionField;
    @FXML private DatePicker taskDueDatePicker;
    @FXML private ComboBox<Priority> taskPriorityComboBox;
    @FXML private Button addTaskButton;
    @FXML private Button updateTaskButton;
    @FXML private Button deleteTaskButton;
    @FXML private Button markTaskCompleteButton;
    @FXML private Button clearTaskFormButton;
    @FXML private Button refreshTasksButton;
    //</editor-fold>

    //<editor-fold desc="FXML Fields - Locations Tab">
    @FXML private TableView<Location> locationTableView;
    @FXML private TableColumn<Location, String> locationNameColumn;
    @FXML private TableColumn<Location, String> locationAddressColumn;
    @FXML private TableColumn<Location, String> locationDescriptionColumn;
    @FXML private TextField locationNameField;
    @FXML private TextField locationAddressField;
    @FXML private TextArea locationDescriptionArea;
    @FXML private Button addLocationButton;
    @FXML private Button updateLocationButton;
    @FXML private Button deleteLocationButton;
    @FXML private Button clearLocationFormButton;
    @FXML private Button refreshLocationsButton;
    //</editor-fold>

    //<editor-fold desc="FXML Fields - Activities Tab">
    @FXML private TableView<Activity> activityTableView;
    @FXML private TableColumn<Activity, String> activityNameColumn;
    @FXML private TableColumn<Activity, LocalDateTime> activityScheduledTimeColumn;
    @FXML private TableColumn<Activity, Integer> activityDurationColumn;
    @FXML private TableColumn<Activity, String> activityEventColumn;
    @FXML private TableColumn<Activity, String> activityLocationColumn;
    @FXML private TableColumn<Activity, String> activityTasksColumn;
    @FXML private TextField activityNameField;
    @FXML private DatePicker activityDatePicker;
    @FXML private TextField activityTimeField;
    @FXML private TextField activityDurationField;
    @FXML private ComboBox<Event> activityEventComboBox;
    @FXML private ComboBox<Location> activityLocationComboBox;
    @FXML private TextArea activityDescriptionArea;
    @FXML private ListView<Task> availableTasksListView;
    @FXML private ListView<Task> selectedTasksListView;
    @FXML private Button addActivityButton;
    @FXML private Button updateActivityButton;
    @FXML private Button deleteActivityButton;
    @FXML private Button clearActivityFormButton;
    @FXML private Button refreshActivitiesButton;
    @FXML private Button assignTaskButton;
    @FXML private Button unassignTaskButton;
    //</editor-fold>
    
    //<editor-fold desc="FXML Fields - Materials Tab">
    @FXML private TableView<TeachingMaterial> materialTableView;
    @FXML private TableColumn<TeachingMaterial, String> materialNameColumn;
    @FXML private TableColumn<TeachingMaterial, String> materialCategoryColumn;
    @FXML private TableColumn<TeachingMaterial, String> materialDescriptionColumn;
    @FXML private TableColumn<TeachingMaterial, LocalDateTime> materialUploadDateColumn;
    @FXML private TableColumn<TeachingMaterial, String> materialFilePathColumn;
    @FXML private TextField materialNameField;
    @FXML private TextField materialCategoryField;
    @FXML private TextArea materialDescriptionArea;
    @FXML private TextField materialFilePathField;
    @FXML private Button browseMaterialFileButton;
    @FXML private Button addMaterialButton;
    @FXML private Button updateMaterialButton;
    @FXML private Button deleteMaterialButton;
    @FXML private Button openMaterialButton;
    @FXML private Button clearMaterialFormButton;
    @FXML private Button refreshMaterialsButton;
    //</editor-fold>

    //<editor-fold desc="FXML Fields - Quick Notes Tab">
    @FXML private ListView<QuickNote> quickNoteListView;
    @FXML private TextArea quickNoteContentArea;
    @FXML private Button saveQuickNoteButton;
    @FXML private Button deleteQuickNoteButton;
    @FXML private Button clearQuickNoteFormButton;
    @FXML private Button refreshQuickNotesButton;
    @FXML private Label quickNoteTimestampLabel;
    //</editor-fold>

    //<editor-fold desc="FXML Fields - Statistics Tab">
    @FXML private Label totalTasksLabel;
    @FXML private Label completedTasksLabel;
    @FXML private Label pendingTasksLabel;
    @FXML private PieChart taskStatusPieChart;
    @FXML private BarChart<String, Number> taskPriorityBarChart; // String for category (priority name), Number for count
    @FXML private Button refreshStatisticsButton;
    //</editor-fold>

    //<editor-fold desc="FXML Fields - Tools Tab">
    @FXML private Button sendRemindersButton;
    //</editor-fold>

    private final EventService eventService;
    private final TaskService taskService;
    private final LocationService locationService;
    private final ActivityService activityService;
    private final NotificationService notificationService;
    private final TeachingMaterialService materialService;
    private final QuickNoteService quickNoteService;

    //<editor-fold desc="State Variables - Calendar">
    private YearMonth currentYearMonth;
    private final ObservableList<Event> monthlyEvents = FXCollections.observableArrayList();
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final DateTimeFormatter LOCAL_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter QUICK_NOTE_TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    //</editor-fold>

    //<editor-fold desc="State Variables - Tasks">
    private final ObservableList<Task> allTasksObservable = FXCollections.observableArrayList();
    private final ObservableList<Task> availableTasksObservable = FXCollections.observableArrayList();
    private final ObservableList<Task> selectedTasksForActivityObservable = FXCollections.observableArrayList();
    //</editor-fold>

    //<editor-fold desc="State Variables - Locations">
    private final ObservableList<Location> allLocationsObservable = FXCollections.observableArrayList();
    //</editor-fold>

    //<editor-fold desc="State Variables - Activities">
    private final ObservableList<Activity> allActivitiesObservable = FXCollections.observableArrayList();
    private final ObservableList<Event> allEventsObservable = FXCollections.observableArrayList();
    //</editor-fold>

    //<editor-fold desc="State Variables - Materials">
    private final ObservableList<TeachingMaterial> allMaterialsObservable = FXCollections.observableArrayList();
    //</editor-fold>

    //<editor-fold desc="State Variables - Quick Notes">
    private final ObservableList<QuickNote> allQuickNotesObservable = FXCollections.observableArrayList();
    private QuickNote currentSelectedQuickNote = null;
    //</editor-fold>

    @Autowired
    public MainWindowController(EventService eventService, TaskService taskService,
                                LocationService locationService, ActivityService activityService,
                                NotificationService notificationService, TeachingMaterialService materialService,
                                QuickNoteService quickNoteService) {
        this.eventService = eventService;
        this.taskService = taskService;
        this.locationService = locationService;
        this.activityService = activityService;
        this.notificationService = notificationService;
        this.materialService = materialService;
        this.quickNoteService = quickNoteService;
    }

    @FXML
    public void initialize() {
        initializeCalendarTab();
        initializeTasksTab();
        initializeLocationsTab();
        initializeActivitiesTab();
        initializeMaterialsTab();
        initializeQuickNotesTab();
        initializeStatisticsTab(); // Added
    }

    //<editor-fold desc="Initialization Methods">
    private void initializeCalendarTab() { currentYearMonth = YearMonth.now(); eventLocationComboBox.setConverter(locationStringConverter()); eventLocationComboBox.setItems(allLocationsObservable); loadLocations(); updateCalendarView(); eventListView.setItems(monthlyEvents); eventListView.setCellFactory(param -> new ListCell<>() { @Override protected void updateItem(Event event, boolean empty) { super.updateItem(event, empty); setText(empty || event == null ? null : String.format("%s (%s - %s)", event.getTitle(), event.getStartTime().format(DATE_TIME_FORMATTER), event.getEndTime().format(DATE_TIME_FORMATTER))); } }); eventListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> { if (newSel != null) populateEventForm(newSel); else clearEventForm(); }); eventStartDatePicker.setValue(LocalDate.now()); eventEndDatePicker.setValue(LocalDate.now()); }
    private void initializeTasksTab() { setupTaskTableView(); loadTasks(); taskPriorityComboBox.setItems(FXCollections.observableArrayList(Priority.values())); taskPriorityComboBox.setValue(Priority.MEDIUM); taskDueDatePicker.setValue(LocalDate.now()); taskTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> { if (newSel != null) populateTaskForm(newSel); else clearTaskForm(); }); }
    private void initializeLocationsTab() { setupLocationTableView(); loadLocations(); locationTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> { if (newSel != null) populateLocationForm(newSel); else clearLocationForm(); }); }
    private void initializeActivitiesTab() { setupActivityTableView(); loadActivities(); activityEventComboBox.setItems(allEventsObservable); activityEventComboBox.setConverter(eventStringConverter()); loadEventsForActivityComboBox(); activityLocationComboBox.setItems(allLocationsObservable); activityLocationComboBox.setConverter(locationStringConverter()); availableTasksListView.setItems(availableTasksObservable); selectedTasksListView.setItems(selectedTasksForActivityObservable); availableTasksListView.setCellFactory(param -> new TaskListCell()); selectedTasksListView.setCellFactory(param -> new TaskListCell()); loadTasksForActivityLists(); activityTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> { if (newSel != null) populateActivityForm(newSel); else clearActivityForm(); }); activityDatePicker.setValue(LocalDate.now()); }
    private void initializeMaterialsTab() { setupMaterialTableView(); loadMaterials(); materialTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> { if (newSel != null) { populateMaterialForm(newSel); updateMaterialButton.setDisable(false); deleteMaterialButton.setDisable(false); openMaterialButton.setDisable(false); } else { clearMaterialForm(); } }); }
    private void initializeQuickNotesTab() { quickNoteListView.setItems(allQuickNotesObservable); quickNoteListView.setCellFactory(param -> new ListCell<QuickNote>() { @Override protected void updateItem(QuickNote item, boolean empty) { super.updateItem(item, empty); if (empty || item == null) { setText(null); } else { String contentPreview = item.getContent().length() > 50 ? item.getContent().substring(0, 50) + "..." : item.getContent(); contentPreview = contentPreview.replace("\n", " "); setText(String.format("%s (Modified: %s)", contentPreview, item.getLastModifiedTimestamp().format(QUICK_NOTE_TIMESTAMP_FORMATTER))); } } }); quickNoteListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> { currentSelectedQuickNote = newSelection; if (newSelection != null) { quickNoteContentArea.setText(newSelection.getContent()); updateQuickNoteTimestampLabel(newSelection); deleteQuickNoteButton.setDisable(false); } }); loadQuickNotes(); updateQuickNoteTimestampLabel(null); }
    
    private void initializeStatisticsTab() {
        // Configure PieChart
        taskStatusPieChart.setLegendVisible(true); // Show legend
        taskStatusPieChart.setAnimated(true); // Enable animation on data changes

        // Configure BarChart (if visible by default or made visible later)
        // taskPriorityBarChart.getXAxis().setLabel("Priority"); // Already set in FXML
        // taskPriorityBarChart.getYAxis().setLabel("Number of Tasks"); // Already set in FXML
        taskPriorityBarChart.setAnimated(true);

        handleRefreshStatistics(); // Initial data load
    }
    //</editor-fold>

    //<editor-fold desc="Calendar Tab Logic - (collapsed for brevity)">
    private void updateCalendarView() { currentMonthYearLabel.setText(currentYearMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy"))); monthlyEvents.setAll(eventService.findEventsByMonth(currentYearMonth));}
    @FXML private void handlePrevMonth() { currentMonthYearLabel = currentMonthYearLabel.minusMonths(1); updateCalendarView(); }
    @FXML private void handleNextMonth() { currentMonthYearLabel = currentMonthYearLabel.plusMonths(1); updateCalendarView(); }
    @FXML private void handleAddEvent() { Optional<LocalDateTime> start = parseDateTime(eventStartDatePicker.getValue(), eventStartTimeField.getText()); Optional<LocalDateTime> end = parseDateTime(eventEndDatePicker.getValue(), eventEndTimeField.getText()); if (eventTitleField.getText().isEmpty() || start.isEmpty() || end.isEmpty()) { showAlert("Validation Error", "Event Title, Start Date/Time, and End Date/Time are required."); return; } if (end.get().isBefore(start.get())) { showAlert("Validation Error", "End time cannot be before start time."); return; } Location selectedLocation = eventLocationComboBox.getValue(); eventService.saveEvent(new Event(eventTitleField.getText(), start.get(), end.get(), eventDescriptionArea.getText(), selectedLocation != null ? selectedLocation.getName() : null)); updateCalendarView(); clearEventForm(); }
    @FXML private void handleUpdateEvent() { Event selected = eventListView.getSelectionModel().getSelectedItem(); if (selected == null) { showAlert("Selection Error", "No event selected."); return; } Optional<LocalDateTime> start = parseDateTime(eventStartDatePicker.getValue(), eventStartTimeField.getText()); Optional<LocalDateTime> end = parseDateTime(eventEndDatePicker.getValue(), eventEndTimeField.getText()); if (eventTitleField.getText().isEmpty() || start.isEmpty() || end.isEmpty()) { showAlert("Validation Error", "Event Title, Start Date/Time, and End Date/Time are required."); return; } if (end.get().isBefore(start.get())) { showAlert("Validation Error", "End time cannot be before start time."); return; } selected.setTitle(eventTitleField.getText()); selected.setStartTime(start.get()); selected.setEndTime(end.get()); selected.setDescription(eventDescriptionArea.getText()); Location selectedLocation = eventLocationComboBox.getValue(); selected.setLocation(selectedLocation != null ? selectedLocation.getName() : null); eventService.saveEvent(selected); updateCalendarView(); clearEventForm(); }
    @FXML private void handleDeleteEvent() { Event selected = eventListView.getSelectionModel().getSelectedItem(); if (selected == null) { showAlert("Selection Error", "No event selected."); return; } confirmAction("Delete Event: " + selected.getTitle(), () -> { eventService.deleteEvent(selected.getId()); updateCalendarView(); clearEventForm(); }); }
    @FXML private void handleClearEventForm() { clearEventForm(); eventListView.getSelectionModel().clearSelection(); }
    private void populateEventForm(Event event) { eventTitleField.setText(event.getTitle()); eventStartDatePicker.setValue(event.getStartTime().toLocalDate()); eventStartTimeField.setText(event.getStartTime().toLocalTime().format(TIME_FORMATTER)); eventEndDatePicker.setValue(event.getEndTime().toLocalDate()); eventEndTimeField.setText(event.getEndTime().toLocalTime().format(TIME_FORMATTER)); if (StringUtils.hasText(event.getLocation())) { allLocationsObservable.stream().filter(loc -> loc.getName().equalsIgnoreCase(event.getLocation())).findFirst().ifPresent(eventLocationComboBox::setValue); } else { eventLocationComboBox.getSelectionModel().clearSelection(); } eventDescriptionArea.setText(event.getDescription()); updateEventButton.setDisable(false); deleteEventButton.setDisable(false); }
    private void clearEventForm() { eventTitleField.clear(); eventStartDatePicker.setValue(LocalDate.now()); eventStartTimeField.clear(); eventEndDatePicker.setValue(LocalDate.now()); eventEndTimeField.clear(); eventLocationComboBox.getSelectionModel().clearSelection(); eventDescriptionArea.clear(); updateEventButton.setDisable(true); deleteEventButton.setDisable(true); }
    //</editor-fold>

    //<editor-fold desc="Task Tab Logic - (collapsed for brevity)">
    private void setupTaskTableView() { taskDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description")); taskDueDateColumn.setCellValueFactory(new PropertyValueFactory<>("dueDate")); taskPriorityColumn.setCellValueFactory(new PropertyValueFactory<>("priority")); taskCompletedColumn.setCellValueFactory(new PropertyValueFactory<>("completed")); taskCreationDateColumn.setCellValueFactory(new PropertyValueFactory<>("creationDate")); taskDueDateColumn.setCellFactory(col -> createFormattedDateCell(LOCAL_DATE_FORMATTER)); taskCreationDateColumn.setCellFactory(col -> createFormattedDateTimeCell(DATE_TIME_FORMATTER)); taskCompletedColumn.setCellFactory(CheckBoxTableCell.forTableColumn(taskCompletedColumn)); taskCompletedColumn.setOnEditCommit(evt -> { Task task = evt.getRowValue(); taskService.markTaskAsCompleted(task.getId(), evt.getNewValue()); loadTasks(); }); taskTableView.setEditable(true); taskTableView.setItems(allTasksObservable); }
    private void loadTasks() { allTasksObservable.setAll(taskService.getAllTasksSorted()); loadTasksForActivityLists(); }
    @FXML private void handleRefreshTasks() { loadTasks(); }
    @FXML private void handleAddTask() { if (taskDescriptionField.getText().isEmpty()) { showAlert("Validation Error", "Task description empty."); return; } taskService.saveTask(new Task(taskDescriptionField.getText(), taskDueDatePicker.getValue(), taskPriorityComboBox.getValue())); loadTasks(); clearTaskForm(); }
    @FXML private void handleUpdateTask() { Task selected = taskTableView.getSelectionModel().getSelectedItem(); if (selected == null) { showAlert("Selection Error", "No task selected."); return; } if (taskDescriptionField.getText().isEmpty()) { showAlert("Validation Error", "Task description empty."); return; } taskService.updateTask(selected.getId(), taskDescriptionField.getText(), taskDueDatePicker.getValue(), taskPriorityComboBox.getValue(), selected.isCompleted()); loadTasks(); clearTaskForm(); }
    @FXML private void handleDeleteTask() { Task selected = taskTableView.getSelectionModel().getSelectedItem(); if (selected == null) { showAlert("Selection Error", "No task selected."); return; } confirmAction("Delete Task: " + selected.getDescription(), () -> { taskService.deleteTask(selected.getId()); loadTasks(); clearTaskForm(); }); }
    @FXML private void handleMarkTaskComplete() { Task selected = taskTableView.getSelectionModel().getSelectedItem(); if (selected == null) { showAlert("Selection Error", "No task selected."); return; } taskService.markTaskAsCompleted(selected.getId(), !selected.isCompleted()); loadTasks(); taskTableView.getSelectionModel().clearSelection(); }
    @FXML private void handleClearTaskForm() { clearTaskForm(); taskTableView.getSelectionModel().clearSelection(); }
    private void populateTaskForm(Task task) { taskDescriptionField.setText(task.getDescription()); taskDueDatePicker.setValue(task.getDueDate()); taskPriorityComboBox.setValue(task.getPriority()); updateTaskButton.setDisable(false); deleteTaskButton.setDisable(false); markTaskCompleteButton.setDisable(false); }
    private void clearTaskForm() { taskDescriptionField.clear(); taskDueDatePicker.setValue(LocalDate.now()); taskPriorityComboBox.setValue(Priority.MEDIUM); updateTaskButton.setDisable(true); deleteTaskButton.setDisable(true); markTaskCompleteButton.setDisable(true); }
    //</editor-fold>

    //<editor-fold desc="Location Tab Logic - (collapsed for brevity)">
    private void setupLocationTableView() { locationNameColumn.setCellValueFactory(new PropertyValueFactory<>("name")); locationAddressColumn.setCellValueFactory(new PropertyValueFactory<>("address")); locationDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description")); locationTableView.setItems(allLocationsObservable); }
    private void loadLocations() { allLocationsObservable.setAll(locationService.getAllLocations()); }
    @FXML private void handleRefreshLocations() { loadLocations(); }
    @FXML private void handleAddLocation() { if (locationNameField.getText().isEmpty()) { showAlert("Validation Error", "Location name empty."); return; } try { locationService.saveLocation(new Location(locationNameField.getText(), locationAddressField.getText(), locationDescriptionArea.getText())); loadLocations(); clearLocationForm(); } catch (LocationService.LocationNameExistsException e) { showAlert("Save Error", e.getMessage()); } }
    @FXML private void handleUpdateLocation() { Location selected = locationTableView.getSelectionModel().getSelectedItem(); if (selected == null) { showAlert("Selection Error", "No location selected."); return; } if (locationNameField.getText().isEmpty()) { showAlert("Validation Error", "Location name empty."); return; } selected.setName(locationNameField.getText()); selected.setAddress(locationAddressField.getText()); selected.setDescription(locationDescriptionArea.getText()); try { locationService.saveLocation(selected); loadLocations(); clearLocationForm(); } catch (LocationService.LocationNameExistsException e) { showAlert("Save Error", e.getMessage()); } }
    @FXML private void handleDeleteLocation() { Location selected = locationTableView.getSelectionModel().getSelectedItem(); if (selected == null) { showAlert("Selection Error", "No location selected."); return; } confirmAction("Delete Location: " + selected.getName(), () -> { locationService.deleteLocation(selected.getId()); loadLocations(); clearLocationForm(); }); }
    @FXML private void handleClearLocationForm() { clearLocationForm(); locationTableView.getSelectionModel().clearSelection(); }
    private void populateLocationForm(Location loc) { locationNameField.setText(loc.getName()); locationAddressField.setText(loc.getAddress()); locationDescriptionArea.setText(loc.getDescription()); updateLocationButton.setDisable(false); deleteLocationButton.setDisable(false); }
    private void clearLocationForm() { locationNameField.clear(); locationAddressField.clear(); locationDescriptionArea.clear(); updateLocationButton.setDisable(true); deleteLocationButton.setDisable(true); }
    //</editor-fold>

    //<editor-fold desc="Activity Tab Logic - (collapsed for brevity)">
    private void setupActivityTableView() { activityNameColumn.setCellValueFactory(new PropertyValueFactory<>("name")); activityScheduledTimeColumn.setCellValueFactory(new PropertyValueFactory<>("scheduledTime")); activityScheduledTimeColumn.setCellFactory(col -> createFormattedDateTimeCell(DATE_TIME_FORMATTER)); activityDurationColumn.setCellValueFactory(new PropertyValueFactory<>("duration")); activityEventColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRelatedEvent() != null ? cellData.getValue().getRelatedEvent().getTitle() : "N/A")); activityLocationColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAssignedLocation() != null ? cellData.getValue().getAssignedLocation().getName() : "N/A")); activityTasksColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRelatedTasks().stream().map(Task::getDescription).collect(Collectors.joining(", ")))); activityTableView.setItems(allActivitiesObservable); }
    private void loadActivities() { allActivitiesObservable.setAll(activityService.getAllActivities()); }
    @FXML private void handleRefreshActivities() { loadActivities(); }
    private void loadEventsForActivityComboBox() { allEventsObservable.setAll(eventService.getAllEvents()); }
    private void loadTasksForActivityLists() { List<Task> currentSelectedTasks = selectedTasksForActivityObservable.stream().toList(); availableTasksObservable.setAll(allTasksObservable.stream().filter(task -> !currentSelectedTasks.contains(task) && !task.isCompleted()).collect(Collectors.toList())); }
    @FXML private void handleAssignTask() { Task selected = availableTasksListView.getSelectionModel().getSelectedItem(); if (selected != null) { availableTasksObservable.remove(selected); selectedTasksForActivityObservable.add(selected); } }
    @FXML private void handleUnassignTask() { Task selected = selectedTasksListView.getSelectionModel().getSelectedItem(); if (selected != null) { selectedTasksForActivityObservable.remove(selected); availableTasksObservable.add(selected); FXCollections.sort(availableTasksObservable, (t1, t2) -> t1.getDescription().compareToIgnoreCase(t2.getDescription())); } }
    @FXML private void handleAddActivity() { Optional<LocalDateTime> scheduledLdt = parseDateTime(activityDatePicker.getValue(), activityTimeField.getText()); if (activityNameField.getText().isEmpty()) { showAlert("Validation Error", "Activity name required."); return; } Integer duration = parseDuration(activityDurationField.getText()); if (duration == null && !activityDurationField.getText().trim().isEmpty()) { showAlert("Validation Error", "Duration must be a valid number (minutes)."); return; } Activity newActivity = new Activity(activityNameField.getText(), activityDescriptionArea.getText(), scheduledLdt.orElse(null), duration); Long eventId = activityEventComboBox.getValue() != null ? activityEventComboBox.getValue().getId() : null; Long locationId = activityLocationComboBox.getValue() != null ? activityLocationComboBox.getValue().getId() : null; List<Long> taskIds = selectedTasksForActivityObservable.stream().map(Task::getId).collect(Collectors.toList()); activityService.saveActivity(newActivity, eventId, taskIds, locationId); loadActivities(); clearActivityForm(); }
    @FXML private void handleUpdateActivity() { Activity selectedActivity = activityTableView.getSelectionModel().getSelectedItem(); if (selectedActivity == null) { showAlert("Selection Error", "No activity selected."); return; } if (activityNameField.getText().isEmpty()) { showAlert("Validation Error", "Activity name required."); return; } Optional<LocalDateTime> scheduledLdt = parseDateTime(activityDatePicker.getValue(), activityTimeField.getText()); Integer duration = parseDuration(activityDurationField.getText()); if (duration == null && !activityDurationField.getText().trim().isEmpty()) { showAlert("Validation Error", "Duration must be a valid number (minutes)."); return; } selectedActivity.setName(activityNameField.getText()); selectedActivity.setDescription(activityDescriptionArea.getText()); selectedActivity.setScheduledTime(scheduledLdt.orElse(null)); selectedActivity.setDuration(duration); Long eventId = activityEventComboBox.getValue() != null ? activityEventComboBox.getValue().getId() : null; Long locationId = activityLocationComboBox.getValue() != null ? activityLocationComboBox.getValue().getId() : null; List<Long> taskIds = selectedTasksForActivityObservable.stream().map(Task::getId).collect(Collectors.toList()); activityService.saveActivity(selectedActivity, eventId, taskIds, locationId); loadActivities(); clearActivityForm(); }
    @FXML private void handleDeleteActivity() { Activity selected = activityTableView.getSelectionModel().getSelectedItem(); if (selected == null) { showAlert("Selection Error", "No activity selected."); return; } confirmAction("Delete Activity: " + selected.getName(), () -> { activityService.deleteActivity(selected.getId()); loadActivities(); clearActivityForm(); }); }
    @FXML private void handleClearActivityForm() { clearActivityForm(); activityTableView.getSelectionModel().clearSelection(); }
    private void populateActivityForm(Activity activity) { activityNameField.setText(activity.getName()); if (activity.getScheduledTime() != null) { activityDatePicker.setValue(activity.getScheduledTime().toLocalDate()); activityTimeField.setText(activity.getScheduledTime().toLocalTime().format(TIME_FORMATTER)); } else { activityDatePicker.setValue(LocalDate.now()); activityTimeField.clear(); } activityDurationField.setText(activity.getDuration() != null ? activity.getDuration().toString() : ""); activityDescriptionArea.setText(activity.getDescription()); activityEventComboBox.setValue(activity.getRelatedEvent()); activityLocationComboBox.setValue(activity.getAssignedLocation()); selectedTasksForActivityObservable.setAll(activity.getRelatedTasks()); loadTasksForActivityLists(); updateActivityButton.setDisable(false); deleteActivityButton.setDisable(false); }
    private void clearActivityForm() { activityNameField.clear(); activityDatePicker.setValue(LocalDate.now()); activityTimeField.clear(); activityDurationField.clear(); activityDescriptionArea.clear(); activityEventComboBox.getSelectionModel().clearSelection(); activityLocationComboBox.getSelectionModel().clearSelection(); selectedTasksForActivityObservable.clear(); loadTasksForActivityLists(); updateActivityButton.setDisable(true); deleteActivityButton.setDisable(true); activityTableView.getSelectionModel().clearSelection(); }
    //</editor-fold>
    
    //<editor-fold desc="Materials Tab Logic - (collapsed for brevity)">
    private void setupMaterialTableView() { materialNameColumn.setCellValueFactory(new PropertyValueFactory<>("name")); materialCategoryColumn.setCellValueFactory(new PropertyValueFactory<>("category")); materialDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description")); materialUploadDateColumn.setCellValueFactory(new PropertyValueFactory<>("uploadDate")); materialUploadDateColumn.setCellFactory(col -> createFormattedDateTimeCell(DATE_TIME_FORMATTER)); materialFilePathColumn.setCellValueFactory(new PropertyValueFactory<>("filePath")); materialTableView.setItems(allMaterialsObservable); }
    private void loadMaterials() { allMaterialsObservable.setAll(materialService.getAllMaterials()); }
    @FXML private void handleRefreshMaterials() { loadMaterials(); }
    @FXML private void handleBrowseMaterialFile() { FileChooser fileChooser = new FileChooser(); fileChooser.setTitle("Select Material File"); File selectedFile = fileChooser.showOpenDialog(getStage()); if (selectedFile != null) { materialFilePathField.setText(selectedFile.getAbsolutePath()); } }
    @FXML private void handleAddMaterial() { if (materialNameField.getText().isEmpty() || materialFilePathField.getText().isEmpty()) { showAlert("Validation Error", "Material Name and File Path are required."); return; } TeachingMaterial newMaterial = new TeachingMaterial(materialNameField.getText(), materialFilePathField.getText(), materialDescriptionArea.getText(), materialCategoryField.getText()); materialService.saveMaterial(newMaterial); loadMaterials(); clearMaterialForm(); }
    @FXML private void handleUpdateMaterial() { TeachingMaterial selectedMaterial = materialTableView.getSelectionModel().getSelectedItem(); if (selectedMaterial == null) { showAlert("Selection Error", "No material selected to update."); return; } if (materialNameField.getText().isEmpty() || materialFilePathField.getText().isEmpty()) { showAlert("Validation Error", "Material Name and File Path are required."); return; } selectedMaterial.setName(materialNameField.getText()); selectedMaterial.setDescription(materialDescriptionArea.getText()); selectedMaterial.setCategory(materialCategoryField.getText()); selectedMaterial.setFilePath(materialFilePathField.getText()); materialService.saveMaterial(selectedMaterial); loadMaterials(); clearMaterialForm(); }
    @FXML private void handleDeleteMaterial() { TeachingMaterial selectedMaterial = materialTableView.getSelectionModel().getSelectedItem(); if (selectedMaterial == null) { showAlert("Selection Error", "No material selected to delete."); return; } confirmAction("Delete Material Info: " + selectedMaterial.getName() + "\n\nNote: This only deletes the application's record. The actual file on your system will NOT be deleted.", () -> { materialService.deleteMaterial(selectedMaterial.getId()); loadMaterials(); clearMaterialForm(); }); }
    @FXML private void handleOpenMaterial() { TeachingMaterial selectedMaterial = materialTableView.getSelectionModel().getSelectedItem(); if (selectedMaterial == null || selectedMaterial.getFilePath() == null || selectedMaterial.getFilePath().isEmpty()) { showAlert("File Error", "No file path specified for the selected material."); return; } try { File fileToOpen = new File(selectedMaterial.getFilePath()); if (fileToOpen.exists()) { if (Desktop.isDesktopSupported()) { Desktop.getDesktop().open(fileToOpen); } else { showAlert("Platform Error", "Desktop operations not supported on this system."); } } else { showAlert("File Error", "File not found at the specified path: " + selectedMaterial.getFilePath()); } } catch (IOException e) { showAlert("File Error", "Could not open file: " + e.getMessage()); } catch (SecurityException e) { showAlert("Security Error", "Not allowed to open file: " + e.getMessage()); } }
    @FXML private void handleClearMaterialForm() { clearMaterialForm(); materialTableView.getSelectionModel().clearSelection(); }
    private void populateMaterialForm(TeachingMaterial material) { materialNameField.setText(material.getName()); materialCategoryField.setText(material.getCategory()); materialDescriptionArea.setText(material.getDescription()); materialFilePathField.setText(material.getFilePath()); updateMaterialButton.setDisable(false); deleteMaterialButton.setDisable(false); openMaterialButton.setDisable(false); }
    private void clearMaterialForm() { materialNameField.clear(); materialCategoryField.clear(); materialDescriptionArea.clear(); materialFilePathField.clear(); materialTableView.getSelectionModel().clearSelection(); updateMaterialButton.setDisable(true); deleteMaterialButton.setDisable(true); openMaterialButton.setDisable(true); }
    //</editor-fold>

    //<editor-fold desc="Quick Notes Tab Logic - (collapsed for brevity)">
    private void loadQuickNotes() { allQuickNotesObservable.setAll(quickNoteService.getAllNotesSorted()); }
    @FXML private void handleRefreshQuickNotes() { loadQuickNotes(); }
    @FXML private void handleSaveQuickNote() { String content = quickNoteContentArea.getText(); if (content == null || content.trim().isEmpty()) { showAlert("Validation Error", "Note content cannot be empty."); return; } if (currentSelectedQuickNote == null) { currentSelectedQuickNote = new QuickNote(content); } else { currentSelectedQuickNote.setContent(content); } QuickNote savedNote = quickNoteService.saveNote(currentSelectedQuickNote); loadQuickNotes(); quickNoteListView.getSelectionModel().select(savedNote); currentSelectedQuickNote = savedNote; updateQuickNoteTimestampLabel(savedNote); }
    @FXML private void handleDeleteQuickNote() { if (currentSelectedQuickNote == null) { showAlert("Selection Error", "No note selected to delete."); return; } confirmAction("Delete Note", "Are you sure you want to delete this note?", () -> { quickNoteService.deleteNote(currentSelectedQuickNote.getId()); loadQuickNotes(); handleClearQuickNoteForm(); }); }
    @FXML private void handleClearQuickNoteForm() { quickNoteListView.getSelectionModel().clearSelection(); currentSelectedQuickNote = null; quickNoteContentArea.clear(); updateQuickNoteTimestampLabel(null); deleteQuickNoteButton.setDisable(true); }
    private void updateQuickNoteTimestampLabel(QuickNote note) { if (note != null) { String created = note.getCreationTimestamp().format(QUICK_NOTE_TIMESTAMP_FORMATTER); String modified = note.getLastModifiedTimestamp().format(QUICK_NOTE_TIMESTAMP_FORMATTER); quickNoteTimestampLabel.setText(String.format("Created: %s | Modified: %s", created, modified)); } else { quickNoteTimestampLabel.setText("Created: N/A | Modified: N/A"); } }
    //</editor-fold>

    //<editor-fold desc="Statistics Tab Logic">
    @FXML
    private void handleRefreshStatistics() {
        long totalTasks = taskService.getTotalTaskCount();
        long completedTasks = taskService.getCompletedTaskCount();
        long pendingTasks = taskService.getPendingTaskCount();

        totalTasksLabel.setText(String.valueOf(totalTasks));
        completedTasksLabel.setText(String.valueOf(completedTasks));
        pendingTasksLabel.setText(String.valueOf(pendingTasks));

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        if (completedTasks > 0) {
            pieChartData.add(new PieChart.Data("Completed", completedTasks));
        }
        if (pendingTasks > 0) {
            pieChartData.add(new PieChart.Data("Pending", pendingTasks));
        }
        
        if (completedTasks == 0 && pendingTasks == 0) {
            // Add a slice to indicate no data, otherwise pie chart might look weird or throw error
            pieChartData.add(new PieChart.Data("No Tasks", 1));
            taskStatusPieChart.setTitle("Task Status Overview (No Tasks)");
        } else {
            taskStatusPieChart.setTitle("Task Status Overview");
        }
        taskStatusPieChart.setData(pieChartData);

        // Optional: Populate BarChart for priority
        Map<Priority, Long> tasksByPriority = taskService.getTaskCountByPriority();
        XYChart.Series<String, Number> prioritySeries = new XYChart.Series<>();
        prioritySeries.setName("Tasks per Priority"); // Legend item

        if (tasksByPriority.isEmpty() && totalTasks > 0) { // If tasks exist but none have priority
             taskPriorityBarChart.setVisible(true); // Or false, depending on desired behavior
             taskPriorityBarChart.setTitle("Tasks by Priority (None Set)");
             // Could add a dummy category to show the chart is active but empty of priority data
        } else if (tasksByPriority.isEmpty() && totalTasks == 0) {
            taskPriorityBarChart.setVisible(false); // No tasks, no priorities to show
        }
        else {
            tasksByPriority.forEach((priority, count) ->
                prioritySeries.getData().add(new XYChart.Data<>(priority.getDisplayName(), count))
            );
            taskPriorityBarChart.getData().clear(); // Clear previous data
            taskPriorityBarChart.getData().add(prioritySeries);
            taskPriorityBarChart.setVisible(true);
            taskPriorityBarChart.setTitle("Tasks by Priority");
        }
    }
    //</editor-fold>

    //<editor-fold desc="Tools Tab Logic - (collapsed for brevity)">
    @FXML private void handleSendReminders() { int eventsReminded = notificationService.sendEventReminders(); int tasksReminded = notificationService.sendTaskReminders(); String message; if (eventsReminded == -1 && tasksReminded == -1) { showAlert("Configuration Error", "Email sending skipped. Please ensure 'spring.mail.host', 'spring.mail.from', and 'teacher.agenda.notification.email' are correctly configured in application.properties and are not using default placeholder values."); } else { message = String.format("Reminders Sent!\nEvents: %d\nTasks: %d", eventsReminded, tasksReminded); if (eventsReminded == 0 && tasksReminded == 0) { message += "\n\nNo upcoming events or due tasks found, or email configuration might be incomplete (check logs)."; } showInformation("Notification Service", message); } }
    //</editor-fold>

    //<editor-fold desc="Utility Methods & Converters">
    private Optional<LocalDateTime> parseDateTime(LocalDate date, String timeStr) { if (date == null || timeStr == null || timeStr.trim().isEmpty()) return Optional.empty(); try { return Optional.of(LocalDateTime.of(date, LocalTime.parse(timeStr.trim(), TIME_FORMATTER))); } catch (DateTimeParseException e) { showAlert("Invalid Time Format", "Use HH:mm (e.g., 09:30)."); return Optional.empty(); } }
    private Integer parseDuration(String durationStr) { if (durationStr == null || durationStr.trim().isEmpty()) return null; try { return Integer.parseInt(durationStr.trim()); } catch (NumberFormatException e) { return null; } }
    private void showAlert(String title, String content) { Alert alert = new Alert(Alert.AlertType.ERROR); alert.setTitle(title); alert.setHeaderText(null); alert.setContentText(content); alert.showAndWait(); }
    private void showInformation(String title, String content) { Alert alert = new Alert(Alert.AlertType.INFORMATION); alert.setTitle(title); alert.setHeaderText(null); alert.setContentText(content); alert.showAndWait(); }
    private void confirmAction(String title, String headerText, Runnable action) { Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION); confirmDialog.setTitle(title); confirmDialog.setHeaderText(headerText); confirmDialog.setContentText("This action cannot be undone."); confirmDialog.showAndWait().filter(ButtonType.YES::equals).ifPresent(bt -> action.run()); }
    private void confirmAction(String headerText, Runnable action) { confirmAction("Confirm Action", headerText, action); }
    private <S, T> TableCell<S, T> createFormattedCell(DateTimeFormatter formatter, java.util.function.Function<T, String> formatterFunction) { return new TableCell<>() { @Override protected void updateItem(T item, boolean empty) { super.updateItem(item, empty); setText(empty || item == null ? null : formatterFunction.apply(item)); } }; }
    private <S> TableCell<S, LocalDate> createFormattedDateCell(DateTimeFormatter formatter) { return createFormattedCell(formatter, item -> item.format(formatter)); }
    private <S> TableCell<S, LocalDateTime> createFormattedDateTimeCell(DateTimeFormatter formatter) { return createFormattedCell(formatter, item -> item.format(formatter)); }
    private StringConverter<Location> locationStringConverter() { return new StringConverter<>() { @Override public String toString(Location loc) { return loc == null ? null : loc.getName(); } @Override public Location fromString(String s) { return null; } }; }
    private StringConverter<Event> eventStringConverter() { return new StringConverter<>() { @Override public String toString(Event event) { return event == null ? null : event.getTitle(); } @Override public Event fromString(String s) { return null; } }; }
    private static class TaskListCell extends ListCell<Task> { @Override protected void updateItem(Task item, boolean empty) { super.updateItem(item, empty); setText(empty || item == null ? null : item.getDescription() + (item.isCompleted() ? " (Completed)" : "")); } }
    private Stage getStage() { if (materialNameField != null && materialNameField.getScene() != null) { return (Stage) materialNameField.getScene().getWindow(); } return null;  }
    //</editor-fold>
}
