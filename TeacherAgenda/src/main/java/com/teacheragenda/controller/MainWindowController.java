package com.teacheragenda.controller;

import com.teacheragenda.config.LocaleManager;
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
import com.teacheragenda.service.ICalService;

import com.dlsc.mapsfx.MapView;
import com.dlsc.mapsfx.MapLabel;
import com.dlsc.mapsfx.Marker;
import com.dlsc.mapsfx.model.Coordinates;
import com.dlsc.mapsfx.model.MapPoint;
import com.dlsc.mapsfx.model.Visibility;

import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxUtils;
import com.mxgraph.util.mxXmlUtils;
import com.mxgraph.view.mxGraph;
import org.w3c.dom.Document;


import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.controlsfx.control.MonthView;
import org.controlsfx.control.Notifications;
import javafx.geometry.Pos;
import javafx.util.Duration;
import javafx.util.StringConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.swing.SwingUtilities;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class MainWindowController {
    private static final Logger logger = LoggerFactory.getLogger(MainWindowController.class);
    private final LocaleManager localeManager = LocaleManager.getInstance();

    //<editor-fold desc="FXML Fields - Calendar Tab">
    @FXML private Label currentMonthYearLabel;
    @FXML private MonthView<Event> calendarMonthView;
    @FXML private ListView<Event> eventListViewForDate;
    @FXML private Button todayButton;
    @FXML private Button importEventsButton;
    @FXML private Button exportEventsButton;
    @FXML private TextField eventTitleField; // UserData will store event ID for updates
    @FXML private DatePicker eventStartDatePicker;
    @FXML private TextField eventStartTimeField;
    @FXML private DatePicker eventEndDatePicker;
    @FXML private TextField eventEndTimeField;
    @FXML private ComboBox<Location> eventLocationComboBox;
    @FXML private TextArea eventDescriptionArea;
    @FXML private Button addEventButton; // Text will change to "Save Changes" if editing
    @FXML private Button updateEventButton; // Will be hidden/removed, addEventButton handles both
    @FXML private Button deleteEventButton;
    @FXML private Button clearEventFormButton;
    @FXML private ToggleButton monthViewToggle;
    @FXML private ToggleButton weekViewToggle;
    @FXML private ToggleButton dayViewToggle;
    private ToggleGroup calendarViewToggleGroup;
    @FXML private StackPane calendarViewStack;
    @FXML private VBox monthViewContainer;
    @FXML private BorderPane weekViewPane;
    @FXML private BorderPane dayViewPane;
    @FXML private Button prevWeekButton;
    @FXML private Label currentWeekRangeLabel;
    @FXML private Button nextWeekButton;
    @FXML private ScrollPane weekScrollPane;
    @FXML private GridPane weekGridPane;
    @FXML private Button prevDayButton;
    @FXML private Label currentDayLabel;
    @FXML private Button nextDayButton;
    @FXML private ScrollPane dayScrollPane;
    @FXML private Pane dayTimelineContainerPane;
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
    @FXML private MapView locationMapView;
    @FXML private TableView<Location> locationTableView;
    @FXML private TableColumn<Location, String> locationNameColumn;
    @FXML private TableColumn<Location, String> locationAddressColumn;
    @FXML private TextField locationNameField;
    @FXML private TextField locationAddressField;
    @FXML private TextField locationLatitudeField;
    @FXML private TextField locationLongitudeField;
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
    @FXML private Button deleteActivityButton;
    @FXML private Button clearActivityFormButton;
    @FXML private Button refreshActivitiesButton;
    @FXML private Button assignTaskButton;
    @FXML private Button unassignTaskButton;
    @FXML private SwingNode diagramSwingNode;
    @FXML private TextArea pseudocodeTextArea;
    @FXML private Button saveDiagramButton;
    @FXML private Button loadDiagramButton;
    @FXML private Button savePseudocodeButton;
    @FXML private Button loadPseudocodeButton;
    @FXML private Button addBoxButton;
    @FXML private Button addCircleButton;
    @FXML private Button addEdgeButton;
    //</editor-fold>

    //<editor-fold desc="FXML Fields - Materials Tab">
    @FXML private TableView<TeachingMaterial> materialTableView;
    @FXML private TableColumn<TeachingMaterial, String> materialNameColumn;
    @FXML private TableColumn<TeachingMaterial, String> materialFileTypeColumn;
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
    @FXML private BarChart<String, Number> taskPriorityBarChart;
    @FXML private Button refreshStatisticsButton;
    //</editor-fold>

    //<editor-fold desc="FXML Fields - Tools Tab">
    @FXML private Button sendRemindersButton;
    @FXML private ComboBox<LocaleWrapper> languageComboBox;
    @FXML private Button saveLanguageButton;
    //</editor-fold>

    private final EventService eventService;
    private final TaskService taskService;
    private final LocationService locationService;
    private final ActivityService activityService;
    private final NotificationService notificationService;
    private final TeachingMaterialService materialService;
    private final QuickNoteService quickNoteService;
    private final ICalService iCalService;

    //<editor-fold desc="State Variables - Calendar">
    private YearMonth currentDisplayedYearMonth;
    private LocalDate currentDisplayedDateForWeekView;
    private LocalDate currentDisplayedDateForDayView;
    private final ObservableList<Event> eventsForSelectedDate = FXCollections.observableArrayList();
    private Set<LocalDate> datesWithEvents = new HashSet<>();
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final DateTimeFormatter LOCAL_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter QUICK_NOTE_TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DataFormat SERIALIZED_MIME_TYPE = new DataFormat("application/x-java-serialized-object");
    //</editor-fold>

    //<editor-fold desc="State Variables - Tasks">
    private final ObservableList<Task> allTasksObservable = FXCollections.observableArrayList();
    private final ObservableList<Task> availableTasksObservable = FXCollections.observableArrayList();
    private final ObservableList<Task> selectedTasksForActivityObservable = FXCollections.observableArrayList();
    //</editor-fold>

    //<editor-fold desc="State Variables - Locations">
    private final ObservableList<Location> allLocationsObservable = FXCollections.observableArrayList();
    private final List<Marker> locationMarkers = new ArrayList<>();
    //</editor-fold>

    //<editor-fold desc="State Variables - Activities">
    private final ObservableList<Activity> allActivitiesObservable = FXCollections.observableArrayList();
    private final ObservableList<Event> allEventsObservable = FXCollections.observableArrayList();
    private mxGraph diagramGraph;
    private mxGraphComponent diagramGraphComponent;
    private Activity currentSelectedActivityForDiagram = null;
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
                                QuickNoteService quickNoteService, ICalService iCalService) {
        this.eventService = eventService;
        this.taskService = taskService;
        this.locationService = locationService;
        this.activityService = activityService;
        this.notificationService = notificationService;
        this.materialService = materialService;
        this.quickNoteService = quickNoteService;
        this.iCalService = iCalService;
    }

    @FXML
    public void initialize() { /* ... */ }
    public void updateUITexts() { /* ... */ }
    private void addTooltips() { /* ... */ }

    //<editor-fold desc="Initialization Methods">
    private void initializeCalendarTab() {
        currentDisplayedYearMonth = YearMonth.now();
        currentDisplayedDateForWeekView = LocalDate.now();
        currentDisplayedDateForDayView = LocalDate.now();
        calendarMonthView.setYearMonth(currentDisplayedYearMonth);
        eventLocationComboBox.setConverter(locationStringConverter());
        eventLocationComboBox.setItems(allLocationsObservable);
        loadLocations();
        calendarMonthView.setDayCellFactory(param -> new EventDayCell());
        calendarMonthView.setShowWeekNumbers(false);
        calendarMonthView.selectedDateProperty().addListener((obs, oldDate, newDate) -> {
            if (newDate != null) {
                loadEventsForSelectedDateInMonthListView(newDate);
                eventStartDatePicker.setValue(newDate);
                eventEndDatePicker.setValue(newDate);
                if (eventListViewForDate.getItems().isEmpty()) {
                    eventTitleField.requestFocus();
                    eventTitleField.setUserData(null); // Clear any existing event ID for new event
                    updateEventButton.setDisable(true);
                    deleteEventButton.setDisable(true);
                    addEventButton.setText(localeManager.getString("calendar.button.addEvent"));
                }
            } else {
                eventsForSelectedDate.clear();
            }
        });
        calendarMonthView.yearMonthProperty().addListener((obs, oldYm, newYm) -> {
            currentDisplayedYearMonth = newYm;
            updateMonthViewLabel();
            loadMonthViewData();
        });
        eventListViewForDate.setItems(eventsForSelectedDate);
        eventListViewForDate.setCellFactory(param -> new ListCell<>() {
            @Override protected void updateItem(Event event, boolean empty) {
                super.updateItem(event, empty);
                setText(empty || event == null ? null : String.format("%s (%s - %s)", event.getTitle(), event.getStartTime().format(DATE_TIME_FORMATTER), event.getEndTime().format(DATE_TIME_FORMATTER)));
            }
        });
        eventListViewForDate.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                populateEventForm(newSelection);
            } else {
                // Don't fully clear if a date is selected in MonthView, keep date pickers
                if (calendarMonthView.getSelectedDate() == null) {
                    clearEventFormInternal();
                }
            }
        });
        eventStartDatePicker.setValue(LocalDate.now());
        eventEndDatePicker.setValue(LocalDate.now());
        initializeCalendarViewSwitching();
        loadMonthViewData();
        // Hide the standalone update button, addEventButton will handle updates
        if(updateEventButton != null) {
            updateEventButton.setVisible(false);
            updateEventButton.setManaged(false);
        }
    }
    private void initializeTasksTab() { /* ... */ }
    private void initializeLocationsTab() { /* ... */ }
    private void initializeActivitiesTab() { /* ... */ }
    private void initializeMaterialsTab() { /* ... */ }
    private void initializeQuickNotesTab() { /* ... */ }
    private void initializeStatisticsTab() { /* ... */ }
    private static class LocaleWrapper { /* ... */ }
    private void initializeLanguageSelection() { /* ... */ }
    @FXML private void handleSaveLanguageAction() { /* ... */ }

    private void initializeCalendarViewSwitching() {
        calendarViewToggleGroup = new ToggleGroup();
        monthViewToggle.setToggleGroup(calendarViewToggleGroup);
        weekViewToggle.setToggleGroup(calendarViewToggleGroup);
        dayViewToggle.setToggleGroup(calendarViewToggleGroup);
        monthViewToggle.setSelected(true);
        showMonthView();
        calendarViewToggleGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle == null) {
                monthViewToggle.setSelected(true);
            } else if (newToggle == monthViewToggle) {
                showMonthView();
            } else if (newToggle == weekViewToggle) {
                showWeekView();
            } else if (newToggle == dayViewToggle) {
                showDayView();
            }
        });
    }

    private void showMonthView() { /* ... */ }
    private void showWeekView() { /* ... */ }
    private void showDayView() { /* ... */ }
    //</editor-fold>

    //<editor-fold desc="Calendar Tab Logic">
    private void updateMonthViewLabel() { /* ... */ }
    private void loadMonthViewData() { /* ... */ }
    private void loadEventsForSelectedDateInMonthListView(LocalDate date) { /* ... */ }
    @FXML private void handleToday() { /* ... */ }
    @FXML private void handlePrevMonth() { /* ... */ }
    @FXML private void handleNextMonth() { /* ... */ }

    private void refreshAllCalendarViews() {
        LocalDate monthSel = calendarMonthView.getSelectedDate();
        // Week and Day views use their own date tracking fields which are preserved
        loadMonthViewData();
        loadWeekViewData();
        loadDayViewData();
        if (monthSel != null) calendarMonthView.setSelectedDate(monthSel); // Re-select date in month view
    }

    @FXML private void handleAddEvent() { // This method now handles both Add and Update
        Optional<LocalDateTime> start = parseDateTime(eventStartDatePicker.getValue(), eventStartTimeField.getText());
        Optional<LocalDateTime> end = parseDateTime(eventEndDatePicker.getValue(), eventEndTimeField.getText());
        if (eventTitleField.getText().isEmpty() || start.isEmpty() || end.isEmpty()) { showAlert(localeManager.getString("alert.title.validationError"), localeManager.getString("alert.content.event.validation")); return; }
        if (end.get().isBefore(start.get())) { showAlert(localeManager.getString("alert.title.validationError"), localeManager.getString("alert.content.event.endTimeBeforeStart")); return; }

        Location selectedLocation = eventLocationComboBox.getValue();
        String locationName = selectedLocation != null ? selectedLocation.getName() : null;

        Event eventToSave;
        boolean isUpdate = false;
        if (eventTitleField.getUserData() instanceof Long) { // Check if an existing event ID is stored
            Long eventId = (Long) eventTitleField.getUserData();
            Optional<Event> existingEventOpt = eventService.getEventById(eventId);
            if (existingEventOpt.isPresent()) {
                eventToSave = existingEventOpt.get();
                isUpdate = true;
            } else {
                showAlert(localeManager.getString("alert.title.error"), "Original event not found for update."); // Should not happen
                return;
            }
        } else {
            eventToSave = new Event();
        }

        eventToSave.setTitle(eventTitleField.getText());
        eventToSave.setStartTime(start.get());
        eventToSave.setEndTime(end.get());
        eventToSave.setDescription(eventDescriptionArea.getText());
        eventToSave.setLocation(locationName);

        eventService.saveEvent(eventToSave);
        refreshAllCalendarViews();
        clearEventForm();
        eventTitleField.requestFocus();
    }

    // handleUpdateEvent is now effectively replaced by handleAddEvent logic
    // @FXML private void handleUpdateEvent() { ... } // Can be removed or commented out

    @FXML private void handleDeleteEvent() {
        Event selected = null;
        if (eventTitleField.getUserData() instanceof Long) { // Check if an event is loaded in the form
             Long eventId = (Long) eventTitleField.getUserData();
             Optional<Event> eventOpt = eventService.getEventById(eventId);
             if (eventOpt.isPresent()) selected = eventOpt.get();
        }
        // Fallback to list view selection if nothing in form's UserData (e.g. user cleared form then selected from list)
        if (selected == null && eventListViewForDate.getSelectionModel().getSelectedItem() != null) {
             selected = eventListViewForDate.getSelectionModel().getSelectedItem();
        }

        if (selected == null) { showAlert(localeManager.getString("alert.title.selectionError"), localeManager.getString("alert.content.event.noneSelectedForDelete")); return; }

        final Event eventToDelete = selected;
        confirmAction(MessageFormat.format(localeManager.getString("alert.header.deleteEvent"), eventToDelete.getTitle()), () -> {
            eventService.deleteEvent(eventToDelete.getId());
            refreshAllCalendarViews();
            clearEventForm();
        });
    }
    @FXML private void handleClearEventForm() {
        LocalDate previouslySelectedDate = calendarMonthView.getSelectedDate();
        if (previouslySelectedDate == null && weekViewToggle.isSelected()) previouslySelectedDate = currentDisplayedDateForWeekView;
        if (previouslySelectedDate == null && dayViewToggle.isSelected()) previouslySelectedDate = currentDisplayedDateForDayView;

        clearEventFormInternal();
        if (previouslySelectedDate != null) {
            eventStartDatePicker.setValue(previouslySelectedDate);
            eventEndDatePicker.setValue(previouslySelectedDate);
        } else {
            eventStartDatePicker.setValue(LocalDate.now());
            eventEndDatePicker.setValue(LocalDate.now());
        }
        eventListViewForDate.getSelectionModel().clearSelection();
        eventTitleField.requestFocus();
    }
    private void populateEventForm(Event event) {
        eventTitleField.setText(event.getTitle());
        eventTitleField.setUserData(event.getId()); // Store ID
        eventStartDatePicker.setValue(event.getStartTime().toLocalDate());
        eventStartTimeField.setText(event.getStartTime().toLocalTime().format(TIME_FORMATTER));
        eventEndDatePicker.setValue(event.getEndTime().toLocalDate());
        eventEndTimeField.setText(event.getEndTime().toLocalTime().format(TIME_FORMATTER));
        if (StringUtils.hasText(event.getLocation())) {
            allLocationsObservable.stream().filter(loc -> loc.getName().equalsIgnoreCase(event.getLocation())).findFirst().ifPresent(eventLocationComboBox::setValue);
        } else {
            eventLocationComboBox.getSelectionModel().clearSelection();
        }
        eventDescriptionArea.setText(event.getDescription());
        addEventButton.setText(localeManager.getString("button.update")); // Change button text to "Update"
        deleteEventButton.setDisable(false);
        eventTitleField.requestFocus();
    }
    private void clearEventFormInternal() {
        eventTitleField.clear();
        eventStartTimeField.clear();
        eventEndTimeField.clear();
        eventLocationComboBox.getSelectionModel().clearSelection();
        eventDescriptionArea.clear();
        addEventButton.setText(localeManager.getString("calendar.button.addEvent")); // Reset button text
        deleteEventButton.setDisable(true);
        eventTitleField.setUserData(null); // Clear stored ID
    }
    private class EventDayCell extends DateCell { /* ... */ }
    @FXML private void handleExportEventsAction() { /* ... */ }
    @FXML private void handleImportEventsAction() { /* ... */ }
    //</editor-fold>

    //<editor-fold desc="Task Tab Logic - (collapsed for brevity)">
    private void setupTaskTableView() { /* ... */ }
    private void setupTaskDragAndDrop() { /* ... */ }
    private void loadTasks() { /* ... */ }
    @FXML private void handleRefreshTasks() { /* ... */ }
    @FXML private void handleAddTask() { /* ... */ }
    @FXML private void handleUpdateTask() { /* ... */ }
    @FXML private void handleDeleteTask() { /* ... */ }
    @FXML private void handleMarkTaskComplete() { /* ... */ }
    @FXML private void handleClearTaskForm() { /* ... */ }
    private void populateTaskForm(Task task) { /* ... */ }
    private void clearTaskForm() { /* ... */ }
    //</editor-fold>

    //<editor-fold desc="Location Tab Logic - (collapsed for brevity)">
    private void setupLocationTableView() { /* ... */ }
    private void loadLocations() { /* ... */ }
    private void updateLocationMarkers() { /* ... */ }
    @FXML private void handleRefreshLocations() { /* ... */ }
    @FXML private void handleAddLocation() { /* ... */ }
    @FXML private void handleUpdateLocation() { /* ... */ }
    @FXML private void handleDeleteLocation() { /* ... */ }
    @FXML private void handleClearLocationForm() { /* ... */ }
    private void populateLocationForm(Location loc) { /* ... */ }
    private void clearLocationForm() { /* ... */ }
    //</editor-fold>

    //<editor-fold desc="Activity Tab Logic - (collapsed for brevity)">
    private void setupActivityTableView() { /* ... */ }
    private void loadActivities() { /* ... */ }
    @FXML private void handleRefreshActivities() { /* ... */ }
    private void loadEventsForActivityComboBox() { /* ... */ }
    private void loadTasksForActivityLists() { /* ... */ }
    @FXML private void handleAssignTask() { /* ... */ }
    @FXML private void handleUnassignTask() { /* ... */ }
    @FXML private void handleAddActivity() { /* ... (ensure this handles create/update for activity details) */ }
    @FXML private void handleDeleteActivity() { /* ... */ }
    @FXML private void handleClearActivityForm() { /* ... */ }
    private void populateActivityForm(Activity activity) { /* ... */ }
    private void clearDiagram() { /* ... */ }
    @FXML private void handleAddBoxToDiagram() { /* ... */ }
    @FXML private void handleAddCircleToDiagram() { /* ... */ }
    @FXML private void handleAddEdgeToDiagram() { /* ... */ }
    @FXML private void handleSaveDiagram() { /* ... */ }
    @FXML private void handleLoadDiagram() { /* ... */ }
    @FXML private void handleSavePseudocode() { /* ... */ }
    @FXML private void handleLoadPseudocode() { /* ... */ }
    //</editor-fold>

    //<editor-fold desc="Materials Tab Logic - (collapsed for brevity)">
    private void setupMaterialTableView() { /* ... */ }
    private void loadMaterials() { /* ... */ }
    @FXML private void handleRefreshMaterials() { /* ... */ }
    @FXML private void handleBrowseMaterialFile() { /* ... */ }
    @FXML private void handleAddMaterial() { /* ... */ }
    @FXML private void handleUpdateMaterial() { /* ... */ }
    @FXML private void handleDeleteMaterial() { /* ... */ }
    @FXML private void handleOpenMaterial() { /* ... */ }
    @FXML private void handleClearMaterialForm() { /* ... */ }
    private void populateMaterialForm(TeachingMaterial material) { /* ... */ }
    private void clearMaterialForm() { /* ... */ }
    //</editor-fold>

    //<editor-fold desc="Quick Notes Tab Logic - (collapsed for brevity)">
    private void loadQuickNotes() { /* ... */ }
    @FXML private void handleRefreshQuickNotes() { /* ... */ }
    @FXML private void handleSaveQuickNote() { /* ... */ }
    @FXML private void handleDeleteQuickNote() { /* ... */ }
    @FXML private void handleClearQuickNoteForm() { /* ... */ }
    private void updateQuickNoteTimestampLabel(QuickNote note) { /* ... */ }
    //</editor-fold>

    //<editor-fold desc="Statistics Tab Logic - (collapsed for brevity)">
    @FXML private void handleRefreshStatistics() { /* ... */ }
    //</editor-fold>

    //<editor-fold desc="Tools Tab Logic - (collapsed for brevity)">
    @FXML private void handleSendReminders() { /* ... */ }
    private void showDesktopNotification(String title, String message) { /* ... */ }
    //</editor-fold>

    //<editor-fold desc="Utility Methods & Converters - (collapsed for brevity)">
    private Optional<LocalDateTime> parseDateTime(LocalDate date, String timeStr) { /* ... */ }
    private Integer parseDuration(String durationStr) { /* ... */ }
    private void showAlert(String title, String content) { /* ... */ }
    private void showInformation(String title, String content) { /* ... */ }
    private void confirmAction(String title, String headerText, Runnable action) { /* ... */ }
    private void confirmAction(String headerText, Runnable action) { /* ... */ }
    private <S, T> TableCell<S, T> createFormattedCell(DateTimeFormatter formatter, java.util.function.Function<T, String> formatterFunction) { /* ... */ }
    private <S> TableCell<S, LocalDate> createFormattedDateCell(DateTimeFormatter formatter) { /* ... */ }
    private <S> TableCell<S, LocalDateTime> createFormattedDateTimeCell(DateTimeFormatter formatter) { /* ... */ }
    private StringConverter<Location> locationStringConverter() { /* ... */ }
    private StringConverter<Event> eventStringConverter() { /* ... */ }
    private class TaskListCell extends ListCell<Task> { /* ... */ }
    private Stage getStage() { /* ... */ }
    //</editor-fold>
}
