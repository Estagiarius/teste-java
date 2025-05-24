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
import javafx.scene.input.TransferMode; 
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.controlsfx.control.MonthView; 
import org.controlsfx.control.Notifications;
import javafx.geometry.Pos;
import javafx.util.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.swing.SwingUtilities; 
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class MainWindowController {

    //<editor-fold desc="FXML Fields - Calendar Tab">
    @FXML private Label currentMonthYearLabel;
    @FXML private MonthView<Event> calendarMonthView; 
    @FXML private ListView<Event> eventListViewForDate; 
    @FXML private Button todayButton; 
    @FXML private Button importEventsButton; // Added for import
    @FXML private Button exportEventsButton; 
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
    //</editor-fold>

    private final EventService eventService;
    private final TaskService taskService;
    private final LocationService locationService;
    private final ActivityService activityService;
    private final NotificationService notificationService;
    private final TeachingMaterialService materialService;
    private final QuickNoteService quickNoteService;
    private final ICalService iCalService; // Added for export

    //<editor-fold desc="State Variables - Calendar">
    private YearMonth currentDisplayedYearMonth; 
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
                                QuickNoteService quickNoteService, ICalService iCalService) { // Added iCalService
        this.eventService = eventService;
        this.taskService = taskService;
        this.locationService = locationService;
        this.activityService = activityService;
        this.notificationService = notificationService;
        this.materialService = materialService;
        this.quickNoteService = quickNoteService;
        this.iCalService = iCalService; // Added
    }

    @FXML
    public void initialize() {
        initializeCalendarTab();
        initializeTasksTab();
        initializeLocationsTab(); 
        initializeActivitiesTab(); 
        initializeMaterialsTab(); 
        initializeQuickNotesTab();
        initializeStatisticsTab(); 
        addTooltips(); // Add tooltips to various controls
    }
    
    private void addTooltips() {
        // Calendar Tab
        clearEventFormButton.setTooltip(new Tooltip("Clears the event form fields. Select a date on the calendar to pre-fill."));
        
        // Tasks Tab
        markTaskCompleteButton.setTooltip(new Tooltip("Toggles the completion status of the selected task."));
        clearTaskFormButton.setTooltip(new Tooltip("Clears the task form fields."));
        taskTableView.setTooltip(new Tooltip("Drag and drop tasks to reorder them within their priority group."));

        // Locations Tab
        clearLocationFormButton.setTooltip(new Tooltip("Clears the location form fields."));
        locationLatitudeField.setTooltip(new Tooltip("Enter latitude in decimal degrees (e.g., 40.7128)."));
        locationLongitudeField.setTooltip(new Tooltip("Enter longitude in decimal degrees (e.g., -74.0060)."));

        // Activities Tab
        clearActivityFormButton.setTooltip(new Tooltip("Clears all fields in the activity details, diagram, and pseudocode tabs."));
        addActivityButton.setTooltip(new Tooltip("Saves all details for the current activity, including any captured diagram XML and pseudocode."));
        addBoxButton.setTooltip(new Tooltip("Adds a rectangular box to the diagram."));
        addCircleButton.setTooltip(new Tooltip("Adds a circular/elliptical shape to the diagram."));
        addEdgeButton.setTooltip(new Tooltip("Adds an edge between two selected shapes in the diagram."));
        saveDiagramButton.setTooltip(new Tooltip("Captures the current diagram as XML. Use 'Add/Save Activity Details' to persist."));
        loadDiagramButton.setTooltip(new Tooltip("Loads the saved diagram for the selected activity."));
        savePseudocodeButton.setTooltip(new Tooltip("Captures the current pseudocode. Use 'Add/Save Activity Details' to persist."));
        loadPseudocodeButton.setTooltip(new Tooltip("Loads the saved pseudocode for the selected activity."));


        // Materials Tab
        clearMaterialFormButton.setTooltip(new Tooltip("Clears the material form fields."));
        browseMaterialFileButton.setTooltip(new Tooltip("Browse for a teaching material file."));
        openMaterialButton.setTooltip(new Tooltip("Opens the selected material file using the system's default application."));
        deleteMaterialButton.setTooltip(new Tooltip("Deletes the metadata record for this material. The actual file is NOT deleted from your system."));

        // Quick Notes Tab
        clearQuickNoteFormButton.setTooltip(new Tooltip("Clears the current note content area to start a new note or clear selection."));
        saveQuickNoteButton.setTooltip(new Tooltip("Saves the current note content."));

        // Statistics Tab
        refreshStatisticsButton.setTooltip(new Tooltip("Reloads and displays the latest task statistics."));

        // Tools Tab
        sendRemindersButton.setTooltip(new Tooltip("Manually triggers sending of email and desktop notifications for upcoming events and due tasks."));
    }


    //<editor-fold desc="Initialization Methods">
    private void initializeCalendarTab() { currentDisplayedYearMonth = YearMonth.now(); calendarMonthView.setYearMonth(currentDisplayedYearMonth); updateMonthViewLabel(); eventLocationComboBox.setConverter(locationStringConverter()); eventLocationComboBox.setItems(allLocationsObservable); loadLocations(); calendarMonthView.setDayCellFactory(param -> new EventDayCell()); calendarMonthView.setShowWeekNumbers(false); calendarMonthView.selectedDateProperty().addListener((obs, oldDate, newDate) -> { if (newDate != null) { loadEventsForSelectedDate(newDate); eventStartDatePicker.setValue(newDate); eventEndDatePicker.setValue(newDate); if (eventListViewForDate.getItems().isEmpty()) eventTitleField.requestFocus(); } else { eventsForSelectedDate.clear(); } }); calendarMonthView.yearMonthProperty().addListener((obs, oldYm, newYm) -> { currentDisplayedYearMonth = newYm; updateMonthViewLabel(); loadEventsForMonthView(); }); eventListViewForDate.setItems(eventsForSelectedDate); eventListViewForDate.setCellFactory(param -> new ListCell<>() { @Override protected void updateItem(Event event, boolean empty) { super.updateItem(event, empty); setText(empty || event == null ? null : String.format("%s (%s - %s)", event.getTitle(), event.getStartTime().format(DATE_TIME_FORMATTER), event.getEndTime().format(DATE_TIME_FORMATTER))); } }); eventListViewForDate.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> { if (newSelection != null) { populateEventForm(newSelection); } else { clearEventFormInternal(); } }); eventStartDatePicker.setValue(LocalDate.now()); eventEndDatePicker.setValue(LocalDate.now()); loadEventsForMonthView(); }
    
    private void initializeTasksTab() { 
        setupTaskTableView(); 
        setupTaskDragAndDrop(); 
        loadTasks(); 
        taskPriorityComboBox.setItems(FXCollections.observableArrayList(Priority.values())); 
        taskPriorityComboBox.setValue(Priority.MEDIUM); 
        taskDueDatePicker.setValue(LocalDate.now()); 
        taskTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> { 
            if (newSel != null) populateTaskForm(newSel); 
            else clearTaskForm(); 
        }); 
    }

    private void initializeLocationsTab() { locationMapView.initialize(); locationMapView.setCenter(new Coordinates(40.7128, -74.0060)); locationMapView.setZoom(3); setupLocationTableView(); loadLocations(); locationTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> { if (newSel != null) { populateLocationForm(newSel); updateLocationButton.setDisable(false); deleteLocationButton.setDisable(false); if (newSel.getLatitude() != null && newSel.getLongitude() != null) { locationMapView.setCenter(new Coordinates(newSel.getLatitude(), newSel.getLongitude())); locationMapView.setZoom(12); } locationNameField.requestFocus(); } else { clearLocationForm(); updateLocationButton.setDisable(true); deleteLocationButton.setDisable(true); } }); locationMapView.addEventHandler(MapPoint.MAP_CLICKED, event -> { MapPoint mapPoint = event.getMapPoint(); System.out.println("Map clicked at: Lat " + mapPoint.getLatitude() + ", Long " + mapPoint.getLongitude()); }); }
    
    private void initializeActivitiesTab() { 
        setupActivityTableView(); 
        loadActivities(); 
        activityEventComboBox.setItems(allEventsObservable); 
        activityEventComboBox.setConverter(eventStringConverter()); 
        loadEventsForActivityComboBox(); 
        activityLocationComboBox.setItems(allLocationsObservable); 
        activityLocationComboBox.setConverter(locationStringConverter()); 
        availableTasksListView.setItems(availableTasksObservable); 
        selectedTasksListView.setItems(selectedTasksForActivityObservable); 
        availableTasksListView.setCellFactory(param -> new TaskListCell()); 
        selectedTasksListView.setCellFactory(param -> new TaskListCell()); 
        loadTasksForActivityLists(); 
        activityTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> { 
            currentSelectedActivityForDiagram = newSel; 
            if (newSel != null) { 
                populateActivityForm(newSel); 
                handleLoadDiagram(); 
                handleLoadPseudocode(); 
                deleteActivityButton.setDisable(false); 
                activityNameField.requestFocus();
            } else { 
                clearActivityForm(); 
                deleteActivityButton.setDisable(true); 
                clearDiagram(); 
                pseudocodeTextArea.clear(); 
            } 
        }); 
        activityDatePicker.setValue(LocalDate.now()); 
        SwingUtilities.invokeLater(() -> { 
            diagramGraph = new mxGraph(); 
            diagramGraphComponent = new mxGraphComponent(diagramGraph); 
            diagramGraphComponent.setConnectable(true); 
            diagramGraphComponent.setToolTips(true); 
            diagramSwingNode.setContent(diagramGraphComponent); 
        }); 
    }
    
    private void initializeMaterialsTab() { 
        setupMaterialTableView(); 
        loadMaterials(); 
        materialTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> { 
            if (newSel != null) { 
                populateMaterialForm(newSel); 
                updateMaterialButton.setDisable(false); 
                deleteMaterialButton.setDisable(false); 
                openMaterialButton.setDisable(false); 
                materialNameField.requestFocus();
            } else { 
                clearMaterialForm(); 
            } 
        }); 
    }
    
    private void initializeQuickNotesTab() { 
        quickNoteListView.setItems(allQuickNotesObservable); 
        quickNoteListView.setCellFactory(param -> new ListCell<QuickNote>() { 
            @Override protected void updateItem(QuickNote item, boolean empty) { 
                super.updateItem(item, empty); 
                if (empty || item == null) { 
                    setText(null); 
                } else { 
                    String contentPreview = item.getContent().length() > 50 ? item.getContent().substring(0, 50) + "..." : item.getContent(); 
                    contentPreview = contentPreview.replace("\n", " "); 
                    setText(String.format("%s (Modified: %s)", contentPreview, item.getLastModifiedTimestamp().format(QUICK_NOTE_TIMESTAMP_FORMATTER))); 
                } 
            } 
        }); 
        quickNoteListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> { 
            currentSelectedQuickNote = newSelection; 
            if (newSelection != null) { 
                quickNoteContentArea.setText(newSelection.getContent()); 
                updateQuickNoteTimestampLabel(newSelection); 
                deleteQuickNoteButton.setDisable(false); 
                quickNoteContentArea.requestFocus(); // Focus on text area when a note is selected
            } else {
                 // Keep content if user just clicked away from a selected note,
                 // delete button gets disabled by clearQuickNoteForm if called
            }
        }); 

        // Context Menu for QuickNoteListView
        ContextMenu contextMenu = new ContextMenu();
        MenuItem deleteMenuItem = new MenuItem("Delete Note");
        deleteMenuItem.setOnAction(event -> {
            if (currentSelectedQuickNote != null) { // Ensure a note is actually selected
                handleDeleteQuickNote();
            }
        });
        deleteMenuItem.disableProperty().bind(quickNoteListView.getSelectionModel().selectedItemProperty().isNull());
        contextMenu.getItems().add(deleteMenuItem);
        quickNoteListView.setContextMenu(contextMenu);

        loadQuickNotes(); 
        updateQuickNoteTimestampLabel(null); 
    }
    
    private void initializeStatisticsTab() { taskStatusPieChart.setLegendVisible(true); taskStatusPieChart.setAnimated(true); taskPriorityBarChart.setAnimated(true); handleRefreshStatistics(); }
    //</editor-fold>

    //<editor-fold desc="Calendar Tab Logic - (collapsed for brevity)">
    private void updateMonthViewLabel() { currentMonthYearLabel.setText(currentDisplayedYearMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")));}
    private void loadEventsForMonthView() { List<Event> eventsThisMonth = eventService.findEventsByMonth(currentDisplayedYearMonth); datesWithEvents.clear(); for (Event event : eventsThisMonth) { datesWithEvents.add(event.getStartTime().toLocalDate()); } calendarMonthView.refresh(); LocalDate selectedDate = calendarMonthView.getSelectedDate(); if (selectedDate != null) { loadEventsForSelectedDate(selectedDate); } else { eventsForSelectedDate.clear(); } }
    private void loadEventsForSelectedDate(LocalDate date) { List<Event> dailyEvents = eventService.findEventsByDay(date); eventsForSelectedDate.setAll(dailyEvents); }
    @FXML private void handleToday() { currentDisplayedYearMonth = YearMonth.now(); calendarMonthView.setYearMonth(currentDisplayedYearMonth); calendarMonthView.setSelectedDate(LocalDate.now()); updateMonthViewLabel(); }
    @FXML private void handlePrevMonth() { currentDisplayedYearMonth = currentDisplayedYearMonth.minusMonths(1); calendarMonthView.setYearMonth(currentDisplayedYearMonth); }
    @FXML private void handleNextMonth() { currentDisplayedYearMonth = currentDisplayedYearMonth.plusMonths(1); calendarMonthView.setYearMonth(currentDisplayedYearMonth); }
    @FXML private void handleAddEvent() { Optional<LocalDateTime> start = parseDateTime(eventStartDatePicker.getValue(), eventStartTimeField.getText()); Optional<LocalDateTime> end = parseDateTime(eventEndDatePicker.getValue(), eventEndTimeField.getText()); if (eventTitleField.getText().isEmpty() || start.isEmpty() || end.isEmpty()) { showAlert("Validation Error", "Event Title, Start Date/Time, and End Date/Time are required."); return; } if (end.get().isBefore(start.get())) { showAlert("Validation Error", "End time cannot be before start time."); return; } Location selectedLocation = eventLocationComboBox.getValue(); eventService.saveEvent(new Event(eventTitleField.getText(), start.get(), end.get(), eventDescriptionArea.getText(), selectedLocation != null ? selectedLocation.getName() : null)); loadEventsForMonthView(); clearEventForm(); eventTitleField.requestFocus(); }
    @FXML private void handleUpdateEvent() { Event selected = eventListViewForDate.getSelectionModel().getSelectedItem(); if (selected == null) { showAlert("Selection Error", "No event selected from the list for the chosen date."); return; } Optional<LocalDateTime> start = parseDateTime(eventStartDatePicker.getValue(), eventStartTimeField.getText()); Optional<LocalDateTime> end = parseDateTime(eventEndDatePicker.getValue(), eventEndTimeField.getText()); if (eventTitleField.getText().isEmpty() || start.isEmpty() || end.isEmpty()) { showAlert("Validation Error", "Event Title, Start Date/Time, and End Date/Time are required."); return; } if (end.get().isBefore(start.get())) { showAlert("Validation Error", "End time cannot be before start time."); return; } selected.setTitle(eventTitleField.getText()); selected.setStartTime(start.get()); selected.setEndTime(end.get()); selected.setDescription(eventDescriptionArea.getText()); Location selectedLocation = eventLocationComboBox.getValue(); selected.setLocation(selectedLocation != null ? selectedLocation.getName() : null); eventService.saveEvent(selected); loadEventsForMonthView(); clearEventForm(); }
    @FXML private void handleDeleteEvent() { Event selected = eventListViewForDate.getSelectionModel().getSelectedItem(); if (selected == null) { showAlert("Selection Error", "No event selected from the list for the chosen date."); return; } confirmAction("Delete Event: " + selected.getTitle(), () -> { eventService.deleteEvent(selected.getId()); loadEventsForMonthView(); clearEventForm(); }); }
    @FXML private void handleClearEventForm() { LocalDate previouslySelectedDate = calendarMonthView.getSelectedDate(); clearEventFormInternal(); if (previouslySelectedDate != null) { eventStartDatePicker.setValue(previouslySelectedDate); eventEndDatePicker.setValue(previouslySelectedDate); } eventListViewForDate.getSelectionModel().clearSelection(); eventTitleField.requestFocus(); }
    private void populateEventForm(Event event) { eventTitleField.setText(event.getTitle()); eventStartDatePicker.setValue(event.getStartTime().toLocalDate()); eventStartTimeField.setText(event.getStartTime().toLocalTime().format(TIME_FORMATTER)); eventEndDatePicker.setValue(event.getEndTime().toLocalDate()); eventEndTimeField.setText(event.getEndTime().toLocalTime().format(TIME_FORMATTER)); if (StringUtils.hasText(event.getLocation())) { allLocationsObservable.stream().filter(loc -> loc.getName().equalsIgnoreCase(event.getLocation())).findFirst().ifPresent(eventLocationComboBox::setValue); } else { eventLocationComboBox.getSelectionModel().clearSelection(); } eventDescriptionArea.setText(event.getDescription()); updateEventButton.setDisable(false); deleteEventButton.setDisable(false); eventTitleField.requestFocus(); }
    private void clearEventFormInternal() { eventTitleField.clear(); eventStartTimeField.clear(); eventEndTimeField.clear(); eventLocationComboBox.getSelectionModel().clearSelection(); eventDescriptionArea.clear(); updateEventButton.setDisable(true); deleteEventButton.setDisable(true); }
    private class EventDayCell extends DateCell { @Override public void updateItem(LocalDate date, boolean empty) { super.updateItem(date, empty); this.getStyleClass().remove("event-day"); this.setTooltip(null); if (empty || date == null) { setText(null); setGraphic(null); } else { if (datesWithEvents.contains(date)) { this.getStyleClass().add("event-day"); long eventCount = eventService.findEventsByDay(date).size(); if (eventCount > 0) { setTooltip(new Tooltip(eventCount + (eventCount == 1 ? " event" : " events"))); } } } } }

    @FXML
    private void handleExportEventsAction() {
        List<com.teacheragenda.model.Event> allEvents = eventService.getAllEvents(); 
        if (allEvents.isEmpty()) {
            showInformation("Export Events", "No events to export.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Events to iCalendar File");
        fileChooser.setInitialFileName("TeacherAgendaEvents.ics");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("iCalendar Files", "*.ics"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        Stage stage = getStage(); // Assumes getStage() can provide the current window
        if (stage == null) {
            showAlert("Error", "Could not determine the application window to show file dialog.");
            return;
        }
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            boolean success = iCalService.exportEventsToICS(allEvents, file.toPath());
            if (success) {
                showInformation("Export Successful", "Events successfully exported to:\n" + file.getAbsolutePath());
            } else {
                showAlert("Export Failed", "Could not export events. Check logs for details.");
            }
        }
    }

    @FXML
    private void handleImportEventsAction() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Import Events from iCalendar File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("iCalendar Files", "*.ics", "*.ical"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        Stage stage = getStage();
        if (stage == null) {
            showAlert("Error", "Could not determine the application window to show file dialog.");
            return;
        }
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            List<com.teacheragenda.model.Event> importedEvents = iCalService.importEventsFromICS(file.toPath());
            if (importedEvents.isEmpty()) {
                // This could be due to an empty file, parsing error (logged by service), or no valid VEVENTs found.
                showInformation("Import Events", "No valid events found in the selected file, or an error occurred during parsing. Check logs for details.");
            } else {
                int successfullySavedCount = 0;
                for (com.teacheragenda.model.Event eventToSave : importedEvents) {
                    try {
                        eventService.saveEvent(eventToSave); // Save each imported event
                        successfullySavedCount++;
                    } catch (Exception e) {
                        // Log individual save error if necessary
                        logger.error("Failed to save imported event: " + eventToSave.getTitle(), e);
                    }
                }
                loadEventsForMonthView(); // Refresh the calendar view
                showInformation("Import Successful", String.format("%d out of %d events successfully imported and saved.", successfullySavedCount, importedEvents.size()));
            }
        }
    }
    //</editor-fold>

    //<editor-fold desc="Task Tab Logic">
    private void setupTaskTableView() { 
        taskDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description")); 
        taskDueDateColumn.setCellValueFactory(new PropertyValueFactory<>("dueDate")); 
        taskPriorityColumn.setCellValueFactory(new PropertyValueFactory<>("priority")); 
        taskCompletedColumn.setCellValueFactory(new PropertyValueFactory<>("completed")); 
        taskCreationDateColumn.setCellValueFactory(new PropertyValueFactory<>("creationDate")); 
        taskDueDateColumn.setCellFactory(col -> createFormattedDateCell(LOCAL_DATE_FORMATTER)); 
        taskCreationDateColumn.setCellFactory(col -> createFormattedDateTimeCell(DATE_TIME_FORMATTER)); 
        taskCompletedColumn.setCellFactory(CheckBoxTableCell.forTableColumn(taskCompletedColumn)); 
        taskCompletedColumn.setOnEditCommit(evt -> { Task task = evt.getRowValue(); taskService.markTaskAsCompleted(task.getId(), evt.getNewValue()); loadTasks(); }); 
        
        // Priority Column Cell Factory for Color Coding
        taskPriorityColumn.setCellFactory(column -> new TableCell<Task, Priority>() {
            @Override
            protected void updateItem(Priority item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item.getDisplayName());
                    switch (item) {
                        case HIGH:
                            setTextFill(Color.RED);
                            // setStyle("-fx-font-weight: bold;"); // Optional: make it bold
                            break;
                        case MEDIUM:
                            setTextFill(Color.ORANGE);
                            break;
                        case LOW:
                            setTextFill(Color.GREEN); // Or Color.BLACK or default
                            break;
                        default:
                            setTextFill(Color.BLACK);
                            break;
                    }
                }
            }
        });
        
        taskTableView.setEditable(true); 
        taskTableView.setItems(allTasksObservable); 
    }
    private void setupTaskDragAndDrop() { taskTableView.setRowFactory(tv -> { TableRow<Task> row = new TableRow<>(); row.setOnDragDetected(event -> { if (!row.isEmpty()) { Integer index = row.getIndex(); Dragboard db = row.startDragAndDrop(TransferMode.MOVE); db.setDragView(row.snapshot(null, null)); ClipboardContent cc = new ClipboardContent(); cc.put(SERIALIZED_MIME_TYPE, index); db.setContent(cc); event.consume(); } }); row.setOnDragOver(event -> { Dragboard db = event.getDragboard(); if (db.hasContent(SERIALIZED_MIME_TYPE)) { if (row.getIndex() != (Integer) db.getContent(SERIALIZED_MIME_TYPE)) { event.acceptTransferModes(TransferMode.MOVE); event.consume(); } } }); row.setOnDragDropped(event -> { Dragboard db = event.getDragboard(); boolean success = false; if (db.hasContent(SERIALIZED_MIME_TYPE)) { int draggedIndex = (Integer) db.getContent(SERIALIZED_MIME_TYPE); int dropIndex; if (row.isEmpty()) { dropIndex = taskTableView.getItems().size() ; } else { dropIndex = row.getIndex(); } List<Task> items = new ArrayList<>(taskTableView.getItems()); Task draggedItem = items.remove(draggedIndex); if (dropIndex > items.size()) { items.add(draggedItem); } else if (draggedIndex < dropIndex) { items.add(dropIndex -1, draggedItem); } else { items.add(dropIndex, draggedItem); } taskService.updateTaskSortOrder(items); success = true; } event.setDropCompleted(success); event.consume(); }); row.setOnDragDone(event -> { if (event.getTransferMode() == TransferMode.MOVE) { loadTasks(); } event.consume(); }); return row; }); }
    private void loadTasks() { allTasksObservable.setAll(taskService.getAllTasksSorted()); loadTasksForActivityLists(); }
    @FXML private void handleRefreshTasks() { loadTasks(); }
    @FXML private void handleAddTask() { if (taskDescriptionField.getText().isEmpty()) { showAlert("Validation Error", "Task description empty."); return; } taskService.saveTask(new Task(taskDescriptionField.getText(), taskDueDatePicker.getValue(), taskPriorityComboBox.getValue())); loadTasks(); clearTaskForm(); taskDescriptionField.requestFocus(); }
    @FXML private void handleUpdateTask() { Task selected = taskTableView.getSelectionModel().getSelectedItem(); if (selected == null) { showAlert("Selection Error", "No task selected."); return; } if (taskDescriptionField.getText().isEmpty()) { showAlert("Validation Error", "Task description empty."); return; } taskService.updateTask(selected.getId(), taskDescriptionField.getText(), taskDueDatePicker.getValue(), taskPriorityComboBox.getValue(), selected.isCompleted()); loadTasks(); clearTaskForm(); }
    @FXML private void handleDeleteTask() { Task selected = taskTableView.getSelectionModel().getSelectedItem(); if (selected == null) { showAlert("Selection Error", "No task selected."); return; } confirmAction("Delete Task: " + selected.getDescription(), () -> { taskService.deleteTask(selected.getId()); loadTasks(); clearTaskForm(); }); }
    @FXML private void handleMarkTaskComplete() { Task selected = taskTableView.getSelectionModel().getSelectedItem(); if (selected == null) { showAlert("Selection Error", "No task selected."); return; } taskService.markTaskAsCompleted(selected.getId(), !selected.isCompleted()); loadTasks(); taskTableView.getSelectionModel().clearSelection(); }
    @FXML private void handleClearTaskForm() { clearTaskForm(); taskTableView.getSelectionModel().clearSelection(); taskDescriptionField.requestFocus(); }
    private void populateTaskForm(Task task) { taskDescriptionField.setText(task.getDescription()); taskDueDatePicker.setValue(task.getDueDate()); taskPriorityComboBox.setValue(task.getPriority()); updateTaskButton.setDisable(false); deleteTaskButton.setDisable(false); markTaskCompleteButton.setDisable(false); taskDescriptionField.requestFocus(); }
    private void clearTaskForm() { taskDescriptionField.clear(); taskDueDatePicker.setValue(LocalDate.now()); taskPriorityComboBox.setValue(Priority.MEDIUM); updateTaskButton.setDisable(true); deleteTaskButton.setDisable(true); markTaskCompleteButton.setDisable(true); }
    //</editor-fold>

    //<editor-fold desc="Location Tab Logic - (collapsed for brevity)">
    private void setupLocationTableView() { locationNameColumn.setCellValueFactory(new PropertyValueFactory<>("name")); locationAddressColumn.setCellValueFactory(new PropertyValueFactory<>("address")); locationTableView.setItems(allLocationsObservable); }
    private void loadLocations() { allLocationsObservable.setAll(locationService.getAllLocations()); updateLocationMarkers(); }
    private void updateLocationMarkers() { for (Marker marker : locationMarkers) { locationMapView.removeMarker(marker); } locationMarkers.clear(); for (Location loc : allLocationsObservable) { if (loc.getLatitude() != null && loc.getLongitude() != null) { Coordinates coords = new Coordinates(loc.getLatitude(), loc.getLongitude()); Marker newMarker = Marker.createProvided(Marker.Provided.BLUE).setPosition(coords).setVisible(true); MapLabel mapLabel = new MapLabel(loc.getName()); mapLabel.setYOffset(-10); newMarker.attachLabel(mapLabel); locationMapView.addMarker(newMarker); locationMarkers.add(newMarker); newMarker.addEventHandler(Marker.MarkerEvent.MARKER_CLICKED, event -> { locationTableView.getSelectionModel().select(loc); locationMapView.setCenter(coords); }); } } }
    @FXML private void handleRefreshLocations() { loadLocations(); }
    @FXML private void handleAddLocation() { if (locationNameField.getText().isEmpty()) { showAlert("Validation Error", "Location name cannot be empty."); return; } Double lat = null, lon = null; try { if (!locationLatitudeField.getText().trim().isEmpty()) lat = Double.parseDouble(locationLatitudeField.getText().trim()); if (!locationLongitudeField.getText().trim().isEmpty()) lon = Double.parseDouble(locationLongitudeField.getText().trim()); } catch (NumberFormatException e) { showAlert("Validation Error", "Latitude and Longitude must be valid numbers."); return; } Location newLocation = new Location(locationNameField.getText(), locationAddressField.getText(), locationDescriptionArea.getText(), lat, lon); try { locationService.saveLocation(newLocation); loadLocations(); clearLocationForm(); locationNameField.requestFocus(); } catch (LocationService.LocationNameExistsException e) { showAlert("Save Error", e.getMessage()); } }
    @FXML private void handleUpdateLocation() { Location selected = locationTableView.getSelectionModel().getSelectedItem(); if (selected == null) { showAlert("Selection Error", "No location selected to update."); return; } if (locationNameField.getText().isEmpty()) { showAlert("Validation Error", "Location name cannot be empty."); return; } Double lat = null, lon = null; try { if (!locationLatitudeField.getText().trim().isEmpty()) lat = Double.parseDouble(locationLatitudeField.getText().trim()); if (!locationLongitudeField.getText().trim().isEmpty()) lon = Double.parseDouble(locationLongitudeField.getText().trim()); } catch (NumberFormatException e) { showAlert("Validation Error", "Latitude and Longitude must be valid numbers."); return; } selected.setName(locationNameField.getText()); selected.setAddress(locationAddressField.getText()); selected.setDescription(locationDescriptionArea.getText()); selected.setLatitude(lat); selected.setLongitude(lon); try { locationService.saveLocation(selected); loadLocations(); clearLocationForm(); } catch (LocationService.LocationNameExistsException e) { showAlert("Save Error", e.getMessage()); } }
    @FXML private void handleDeleteLocation() { Location selected = locationTableView.getSelectionModel().getSelectedItem(); if (selected == null) { showAlert("Selection Error", "No location selected to delete."); return; } confirmAction("Delete Location: " + selected.getName(), () -> { locationService.deleteLocation(selected.getId()); loadLocations(); clearLocationForm(); }); }
    @FXML private void handleClearLocationForm() { clearLocationForm(); locationTableView.getSelectionModel().clearSelection(); locationNameField.requestFocus(); }
    private void populateLocationForm(Location loc) { locationNameField.setText(loc.getName()); locationAddressField.setText(loc.getAddress()); locationDescriptionArea.setText(loc.getDescription()); locationLatitudeField.setText(loc.getLatitude() != null ? String.format("%.6f", loc.getLatitude()) : ""); locationLongitudeField.setText(loc.getLongitude() != null ? String.format("%.6f", loc.getLongitude()) : ""); updateLocationButton.setDisable(false); deleteLocationButton.setDisable(false); locationNameField.requestFocus(); }
    private void clearLocationForm() { locationNameField.clear(); locationAddressField.clear(); locationDescriptionArea.clear(); locationLatitudeField.clear(); locationLongitudeField.clear(); updateLocationButton.setDisable(true); deleteLocationButton.setDisable(true); }
    //</editor-fold>

    //<editor-fold desc="Activity Tab Logic - (collapsed for brevity)">
    private void setupActivityTableView() { activityNameColumn.setCellValueFactory(new PropertyValueFactory<>("name")); activityScheduledTimeColumn.setCellValueFactory(new PropertyValueFactory<>("scheduledTime")); activityScheduledTimeColumn.setCellFactory(col -> createFormattedDateTimeCell(DATE_TIME_FORMATTER)); activityTableView.setItems(allActivitiesObservable); }
    private void loadActivities() { allActivitiesObservable.setAll(activityService.getAllActivities()); }
    @FXML private void handleRefreshActivities() { loadActivities(); }
    private void loadEventsForActivityComboBox() { allEventsObservable.setAll(eventService.getAllEvents()); }
    private void loadTasksForActivityLists() { List<Task> currentSelectedTasks = selectedTasksForActivityObservable.stream().toList(); availableTasksObservable.setAll(allTasksObservable.stream().filter(task -> !currentSelectedTasks.contains(task) && !task.isCompleted()).collect(Collectors.toList())); }
    @FXML private void handleAssignTask() { Task selected = availableTasksListView.getSelectionModel().getSelectedItem(); if (selected != null) { availableTasksObservable.remove(selected); selectedTasksForActivityObservable.add(selected); } }
    @FXML private void handleUnassignTask() { Task selected = selectedTasksListView.getSelectionModel().getSelectedItem(); if (selected != null) { selectedTasksForActivityObservable.remove(selected); availableTasksObservable.add(selected); FXCollections.sort(availableTasksObservable, (t1, t2) -> t1.getDescription().compareToIgnoreCase(t2.getDescription())); } }
    @FXML private void handleAddActivity() { Activity activityToSave; if (currentSelectedActivityForDiagram != null) { activityToSave = currentSelectedActivityForDiagram; } else { activityToSave = new Activity(); } if (activityNameField.getText().isEmpty()) { showAlert("Validation Error", "Activity name required."); return; } activityToSave.setName(activityNameField.getText()); activityToSave.setDescription(activityDescriptionArea.getText()); Optional<LocalDateTime> scheduledLdt = parseDateTime(activityDatePicker.getValue(), activityTimeField.getText()); activityToSave.setScheduledTime(scheduledLdt.orElse(null)); Integer duration = parseDuration(activityDurationField.getText()); if (duration == null && !activityDurationField.getText().trim().isEmpty()) { showAlert("Validation Error", "Duration must be a valid number (minutes)."); return; } activityToSave.setDuration(duration); if (diagramGraph != null) { try { Document xmlDoc = mxXmlUtils.showMxGraph(diagramGraph); activityToSave.setDiagramXml(mxXmlUtils.getXml(xmlDoc.getDocumentElement())); } catch (Exception e) { System.err.println("Error getting diagram XML: " + e.getMessage());} } activityToSave.setPseudocode(pseudocodeTextArea.getText()); Long eventId = activityEventComboBox.getValue() != null ? activityEventComboBox.getValue().getId() : null; Long locationId = activityLocationComboBox.getValue() != null ? activityLocationComboBox.getValue().getId() : null; List<Long> taskIds = selectedTasksForActivityObservable.stream().map(Task::getId).collect(Collectors.toList()); Activity savedActivity = activityService.saveActivity(activityToSave, eventId, taskIds, locationId); loadActivities(); activityTableView.getSelectionModel().select(savedActivity); currentSelectedActivityForDiagram = savedActivity; showInformation("Success", "Activity details, diagram, and pseudocode saved."); }
    @FXML private void handleDeleteActivity() { Activity selected = activityTableView.getSelectionModel().getSelectedItem(); if (selected == null) { showAlert("Selection Error", "No activity selected."); return; } confirmAction("Delete Activity: " + selected.getName(), () -> { activityService.deleteActivity(selected.getId()); loadActivities(); clearActivityForm(); }); }
    @FXML private void handleClearActivityForm() { activityTableView.getSelectionModel().clearSelection(); currentSelectedActivityForDiagram = null; activityNameField.clear(); activityDatePicker.setValue(LocalDate.now()); activityTimeField.clear(); activityDurationField.clear(); activityDescriptionArea.clear(); activityEventComboBox.getSelectionModel().clearSelection(); activityLocationComboBox.getSelectionModel().clearSelection(); selectedTasksForActivityObservable.clear(); loadTasksForActivityLists(); deleteActivityButton.setDisable(true); clearDiagram(); pseudocodeTextArea.clear(); activityNameField.requestFocus(); }
    private void populateActivityForm(Activity activity) { activityNameField.setText(activity.getName()); if (activity.getScheduledTime() != null) { activityDatePicker.setValue(activity.getScheduledTime().toLocalDate()); activityTimeField.setText(activity.getScheduledTime().toLocalTime().format(TIME_FORMATTER)); } else { activityDatePicker.setValue(LocalDate.now()); activityTimeField.clear(); } activityDurationField.setText(activity.getDuration() != null ? activity.getDuration().toString() : ""); activityDescriptionArea.setText(activity.getDescription()); activityEventComboBox.setValue(activity.getRelatedEvent()); activityLocationComboBox.setValue(activity.getAssignedLocation()); selectedTasksForActivityObservable.setAll(activity.getRelatedTasks()); loadTasksForActivityLists(); deleteActivityButton.setDisable(false); activityNameField.requestFocus(); }
    private void clearDiagram() { if (diagramGraph != null) { diagramGraph.removeCells(diagramGraph.getChildVertices(diagramGraph.getDefaultParent())); } }
    @FXML private void handleAddBoxToDiagram() { if (diagramGraph == null) return; diagramGraph.getModel().beginUpdate(); try { diagramGraph.insertVertex(diagramGraph.getDefaultParent(), null, "Box", 20, 20, 80, 30); } finally { diagramGraph.getModel().endUpdate(); } }
    @FXML private void handleAddCircleToDiagram() { if (diagramGraph == null) return; diagramGraph.getModel().beginUpdate(); try { diagramGraph.insertVertex(diagramGraph.getDefaultParent(), null, "Circle", 50, 80, 50, 50, "shape=ellipse;perimeter=ellipsePerimeter"); } finally { diagramGraph.getModel().endUpdate(); } }
    @FXML private void handleAddEdgeToDiagram() { if (diagramGraph == null) return; Object[] selectionCells = diagramGraph.getSelectionCells(); if (selectionCells != null && selectionCells.length == 2) { diagramGraph.getModel().beginUpdate(); try { diagramGraph.insertEdge(diagramGraph.getDefaultParent(), null, "", selectionCells[0], selectionCells[1]); } finally { diagramGraph.getModel().endUpdate(); } } else { showAlert("Diagram Error", "Select two shapes to connect."); } }
    @FXML private void handleSaveDiagram() { if (currentSelectedActivityForDiagram == null) { showAlert("Error", "No activity selected to save diagram for."); return; } if (diagramGraph == null) { showAlert("Error", "Diagram component not initialized."); return; } try { Document xmlDoc = mxXmlUtils.showMxGraph(diagramGraph); String diagramXml = mxXmlUtils.getXml(xmlDoc.getDocumentElement()); currentSelectedActivityForDiagram.setDiagramXml(diagramXml); showInformation("Diagram", "Diagram XML captured. Click 'Add/Save Activity Details' to persist all changes."); } catch (Exception e) { showAlert("Diagram Error", "Could not capture diagram XML: " + e.getMessage()); } }
    @FXML private void handleLoadDiagram() { if (currentSelectedActivityForDiagram == null || diagramGraph == null) { clearDiagram(); return; } clearDiagram(); String diagramXml = currentSelectedActivityForDiagram.getDiagramXml(); if (diagramXml != null && !diagramXml.isEmpty()) { try { Document document = mxXmlUtils.parseXml(diagramXml); mxGraph tempGraph = new mxGraph(); com.mxgraph.io.mxCodec codec = new com.mxgraph.io.mxCodec(document); codec.decode(document.getDocumentElement(), tempGraph.getModel()); diagramGraph.getModel().beginUpdate(); try { Object[] cells = tempGraph.cloneCells(tempGraph.getChildVertices(tempGraph.getDefaultParent())); diagramGraph.addCells(cells); } finally { diagramGraph.getModel().endUpdate(); } } catch (Exception e) { showAlert("Diagram Error", "Could not load diagram: " + e.getMessage()); } } }
    @FXML private void handleSavePseudocode() { if (currentSelectedActivityForDiagram == null) { showAlert("Error", "No activity selected to save pseudocode for."); return; } currentSelectedActivityForDiagram.setPseudocode(pseudocodeTextArea.getText()); showInformation("Pseudocode", "Pseudocode captured. Click 'Add/Save Activity Details' to persist all changes."); }
    @FXML private void handleLoadPseudocode() { if (currentSelectedActivityForDiagram == null) { pseudocodeTextArea.clear(); return; } pseudocodeTextArea.setText(currentSelectedActivityForDiagram.getPseudocode() != null ? currentSelectedActivityForDiagram.getPseudocode() : ""); }
    //</editor-fold>
    
    //<editor-fold desc="Materials Tab Logic">
    private void setupMaterialTableView() { materialNameColumn.setCellValueFactory(new PropertyValueFactory<>("name")); materialFileTypeColumn.setCellValueFactory(cellData -> { String filePath = cellData.getValue().getFilePath(); String extension = ""; if (filePath != null && filePath.contains(".")) { extension = filePath.substring(filePath.lastIndexOf(".") + 1).toUpperCase(); } switch (extension) { case "PDF": return new SimpleStringProperty("[PDF]"); case "DOC": case "DOCX": return new SimpleStringProperty("[DOC]"); case "XLS": case "XLSX": return new SimpleStringProperty("[XLS]"); case "PPT": case "PPTX": return new SimpleStringProperty("[PPT]"); case "TXT": return new SimpleStringProperty("[TXT]"); case "JPG": case "JPEG": case "PNG": case "GIF": return new SimpleStringProperty("[IMG]"); case "MP4": case "AVI": case "MOV": case "WMV": return new SimpleStringProperty("[VID]"); case "MP3": case "WAV": case "AAC": return new SimpleStringProperty("[SND]"); default: return new SimpleStringProperty(filePath == null || filePath.trim().isEmpty() ? "" : "[FILE]"); } }); materialCategoryColumn.setCellValueFactory(new PropertyValueFactory<>("category")); materialDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description")); materialUploadDateColumn.setCellValueFactory(new PropertyValueFactory<>("uploadDate")); materialUploadDateColumn.setCellFactory(col -> createFormattedDateTimeCell(DATE_TIME_FORMATTER)); materialFilePathColumn.setCellValueFactory(new PropertyValueFactory<>("filePath")); materialTableView.setItems(allMaterialsObservable); }
    private void loadMaterials() { allMaterialsObservable.setAll(materialService.getAllMaterials()); }
    @FXML private void handleRefreshMaterials() { loadMaterials(); }
    @FXML private void handleBrowseMaterialFile() { FileChooser fileChooser = new FileChooser(); fileChooser.setTitle("Select Material File"); File selectedFile = fileChooser.showOpenDialog(getStage()); if (selectedFile != null) { materialFilePathField.setText(selectedFile.getAbsolutePath()); } }
    @FXML private void handleAddMaterial() { if (materialNameField.getText().isEmpty() || materialFilePathField.getText().isEmpty()) { showAlert("Validation Error", "Material Name and File Path are required."); return; } TeachingMaterial newMaterial = new TeachingMaterial(materialNameField.getText(), materialFilePathField.getText(), materialDescriptionArea.getText(), materialCategoryField.getText()); materialService.saveMaterial(newMaterial); loadMaterials(); clearMaterialForm(); materialNameField.requestFocus(); }
    @FXML private void handleUpdateMaterial() { TeachingMaterial selectedMaterial = materialTableView.getSelectionModel().getSelectedItem(); if (selectedMaterial == null) { showAlert("Selection Error", "No material selected to update."); return; } if (materialNameField.getText().isEmpty() || materialFilePathField.getText().isEmpty()) { showAlert("Validation Error", "Material Name and File Path are required."); return; } selectedMaterial.setName(materialNameField.getText()); selectedMaterial.setDescription(materialDescriptionArea.getText()); selectedMaterial.setCategory(materialCategoryField.getText()); selectedMaterial.setFilePath(materialFilePathField.getText()); materialService.saveMaterial(selectedMaterial); loadMaterials(); clearMaterialForm(); }
    @FXML private void handleDeleteMaterial() { TeachingMaterial selectedMaterial = materialTableView.getSelectionModel().getSelectedItem(); if (selectedMaterial == null) { showAlert("Selection Error", "No material selected to delete."); return; } confirmAction( "Delete Material Information", "Are you sure you want to delete the record for '" + selectedMaterial.getName() + "'?\n\n" + "Note: This only deletes the application's record. The actual file on your system will NOT be deleted.", () -> { materialService.deleteMaterial(selectedMaterial.getId()); loadMaterials(); clearMaterialForm(); } ); }
    @FXML private void handleOpenMaterial() { TeachingMaterial selectedMaterial = materialTableView.getSelectionModel().getSelectedItem(); if (selectedMaterial == null || selectedMaterial.getFilePath() == null || selectedMaterial.getFilePath().isEmpty()) { showAlert("File Error", "No file path specified for the selected material."); return; } try { File fileToOpen = new File(selectedMaterial.getFilePath()); if (fileToOpen.exists()) { if (Desktop.isDesktopSupported()) { Desktop.getDesktop().open(fileToOpen); } else { showAlert("Platform Error", "Desktop operations not supported on this system."); } } else { showAlert("File Error", "File not found at the specified path: " + selectedMaterial.getFilePath()); } } catch (IOException e) { showAlert("File Error", "Could not open file: " + e.getMessage()); } catch (SecurityException e) { showAlert("Security Error", "Not allowed to open file: " + e.getMessage()); } }
    @FXML private void handleClearMaterialForm() { clearMaterialForm(); materialTableView.getSelectionModel().clearSelection(); materialNameField.requestFocus(); }
    private void populateMaterialForm(TeachingMaterial material) { materialNameField.setText(material.getName()); materialCategoryField.setText(material.getCategory()); materialDescriptionArea.setText(material.getDescription()); materialFilePathField.setText(material.getFilePath()); updateMaterialButton.setDisable(false); deleteMaterialButton.setDisable(false); openMaterialButton.setDisable(false); materialNameField.requestFocus(); }
    private void clearMaterialForm() { materialNameField.clear(); materialCategoryField.clear(); materialDescriptionArea.clear(); materialFilePathField.clear(); materialTableView.getSelectionModel().clearSelection(); updateMaterialButton.setDisable(true); deleteMaterialButton.setDisable(true); openMaterialButton.setDisable(true); }
    //</editor-fold>

    //<editor-fold desc="Quick Notes Tab Logic">
    private void loadQuickNotes() { allQuickNotesObservable.setAll(quickNoteService.getAllNotesSorted()); }
    @FXML private void handleRefreshQuickNotes() { loadQuickNotes(); }
    @FXML private void handleSaveQuickNote() { String content = quickNoteContentArea.getText(); if (content == null || content.trim().isEmpty()) { showAlert("Validation Error", "Note content cannot be empty."); return; } if (currentSelectedQuickNote == null) { currentSelectedQuickNote = new QuickNote(content); } else { currentSelectedQuickNote.setContent(content); } QuickNote savedNote = quickNoteService.saveNote(currentSelectedQuickNote); loadQuickNotes(); quickNoteListView.getSelectionModel().select(savedNote); currentSelectedQuickNote = savedNote; updateQuickNoteTimestampLabel(savedNote); }
    @FXML private void handleDeleteQuickNote() { if (currentSelectedQuickNote == null) { // Also check selection model if robust delete from context menu is needed
        QuickNote selectedFromList = quickNoteListView.getSelectionModel().getSelectedItem();
        if (selectedFromList == null) {
             showAlert("Selection Error", "No note selected to delete."); return;
        }
        currentSelectedQuickNote = selectedFromList; // Ensure current is set if context menu used
     } 
     confirmAction("Delete Note", "Are you sure you want to delete this note?", () -> { quickNoteService.deleteNote(currentSelectedQuickNote.getId()); loadQuickNotes(); handleClearQuickNoteForm(); }); 
    }
    @FXML private void handleClearQuickNoteForm() { quickNoteListView.getSelectionModel().clearSelection(); currentSelectedQuickNote = null; quickNoteContentArea.clear(); updateQuickNoteTimestampLabel(null); deleteQuickNoteButton.setDisable(true); quickNoteContentArea.requestFocus(); }
    private void updateQuickNoteTimestampLabel(QuickNote note) { if (note != null) { String created = note.getCreationTimestamp().format(QUICK_NOTE_TIMESTAMP_FORMATTER); String modified = note.getLastModifiedTimestamp().format(QUICK_NOTE_TIMESTAMP_FORMATTER); quickNoteTimestampLabel.setText(String.format("Created: %s | Modified: %s", created, modified)); } else { quickNoteTimestampLabel.setText("Created: N/A | Modified: N/A"); } }
    //</editor-fold>

    //<editor-fold desc="Statistics Tab Logic">
    @FXML 
    private void handleRefreshStatistics() {
        refreshStatisticsButton.setDisable(true);
        try {
            long totalTasks = taskService.getTotalTaskCount();
            long completedTasks = taskService.getCompletedTaskCount();
            long pendingTasks = taskService.getPendingTaskCount();

            totalTasksLabel.setText(String.valueOf(totalTasks));
            completedTasksLabel.setText(String.valueOf(completedTasks));
            pendingTasksLabel.setText(String.valueOf(pendingTasks));

            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
            double totalForPercentage = completedTasks + pendingTasks;

            if (totalForPercentage == 0) {
                pieChartData.add(new PieChart.Data("No Tasks", 1)); // Value doesn't matter if only one slice
                taskStatusPieChart.setTitle("Task Status Overview (No Tasks)");
            } else {
                 if (completedTasks > 0) {
                    double percentage = (completedTasks / totalForPercentage) * 100;
                    pieChartData.add(new PieChart.Data(String.format("Completed (%d - %.1f%%)", completedTasks, percentage), completedTasks));
                }
                if (pendingTasks > 0) {
                    double percentage = (pendingTasks / totalForPercentage) * 100;
                    pieChartData.add(new PieChart.Data(String.format("Pending (%d - %.1f%%)", pendingTasks, percentage), pendingTasks));
                }
                taskStatusPieChart.setTitle("Task Status Overview");
            }
            taskStatusPieChart.setData(pieChartData);

            Map<Priority, Long> tasksByPriority = taskService.getTaskCountByPriority();
            XYChart.Series<String, Number> prioritySeries = new XYChart.Series<>();
            prioritySeries.setName("Tasks per Priority"); 

            if (tasksByPriority.isEmpty() && totalTasks > 0) {
                 taskPriorityBarChart.setVisible(true); 
                 taskPriorityBarChart.setTitle("Tasks by Priority (None Set)");
                 taskPriorityBarChart.getData().clear(); // Clear any old data
            } else if (tasksByPriority.isEmpty() && totalTasks == 0) {
                taskPriorityBarChart.setVisible(false); 
            } else {
                tasksByPriority.forEach((priority, count) ->
                    prioritySeries.getData().add(new XYChart.Data<>(priority.getDisplayName(), count))
                );
                taskPriorityBarChart.getData().clear(); 
                taskPriorityBarChart.getData().add(prioritySeries);
                taskPriorityBarChart.setVisible(true);
                taskPriorityBarChart.setTitle("Tasks by Priority");
            }
        } finally {
            refreshStatisticsButton.setDisable(false);
        }
    }
    //</editor-fold>

    //<editor-fold desc="Tools Tab Logic - (collapsed for brevity)">
    @FXML private void handleSendReminders() { List<NotificationService.DesktopNotification> eventNotifications = notificationService.generateEventRemindersContent(); List<NotificationService.DesktopNotification> taskNotifications = notificationService.generateTaskRemindersContent(); int desktopNotificationsShown = 0; int emailsSent = 0; boolean emailConfigError = false; for (NotificationService.DesktopNotification notification : eventNotifications) { showDesktopNotification(notification.getTitle(), notification.getMessage()); desktopNotificationsShown++; if (notificationService.sendEmailNotification(notification)) { emailsSent++; } else { if ("smtp.example.com".equals(System.getProperty("spring.mail.host", "smtp.example.com")) || "user@example.com".equals(System.getProperty("spring.mail.username", "user@example.com"))) { emailConfigError = true; } } } for (NotificationService.DesktopNotification notification : taskNotifications) { showDesktopNotification(notification.getTitle(), notification.getMessage()); desktopNotificationsShown++; if (notificationService.sendEmailNotification(notification)) { emailsSent++; } else { if ("smtp.example.com".equals(System.getProperty("spring.mail.host", "smtp.example.com")) || "user@example.com".equals(System.getProperty("spring.mail.username", "user@example.com"))) { emailConfigError = true; } } } String summaryMessage; if (desktopNotificationsShown == 0) { summaryMessage = "No reminders to send (no upcoming events or due tasks)."; } else { summaryMessage = String.format("Desktop notifications shown: %d\nEmails attempted: %d (sent: %d)", desktopNotificationsShown, eventNotifications.size() + taskNotifications.size(), emailsSent); } if (emailConfigError) { summaryMessage += "\n\nEmail sending might be skipped or failing. Please ensure 'spring.mail.host', 'spring.mail.username', 'spring.mail.password', 'spring.mail.from', and 'teacher.agenda.notification.email' are correctly configured in application.properties and are not using default placeholder values."; showAlert("Email Configuration Issue", summaryMessage); } else { showInformation("Reminders Processed", summaryMessage); } }
    private void showDesktopNotification(String title, String message) { Node ownerNode = sendRemindersButton != null ? sendRemindersButton.getScene().getRoot() : null; Platform.runLater(() -> { Notifications notificationBuilder = Notifications.create().title(title).text(message).graphic(null).hideAfter(Duration.seconds(15)).position(Pos.BOTTOM_RIGHT); if (ownerNode != null) { notificationBuilder.owner(ownerNode); } else { System.err.println("Warning: Could not determine owner for desktop notification. It might not display correctly."); } notificationBuilder.showInformation(); }); }
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
    private Stage getStage() { if (materialNameField != null && materialNameField.getScene() != null) { return (Stage) materialNameField.getScene().getWindow(); } else if (locationMapView != null && locationMapView.getScene() != null) { return (Stage) locationMapView.getScene().getWindow(); } else if (diagramSwingNode != null && diagramSwingNode.getScene() != null) {return (Stage) diagramSwingNode.getScene().getWindow(); } return null;  }
    //</editor-fold>
}
