package cityrecommend;


import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.JOptionPane;
import javax.swing.JLabel;
import javax.swing.event.MouseInputListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.JCheckBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JSeparator;
import javax.swing.JTable;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import comparators.GeodesicCompare;
import comparators.ScoreCompare;
import comparators.TimestampCompare;

import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ListSelectionModel;


public class ControllerGUI extends JFrame implements MouseInputListener, ActionListener, WindowListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8395609496282147027L;	
	private static final String[] DEFAULT_TERMS = new String[] {"bar","beach","restaurant","museum","hotel","transport","temple"};
	private static final String FILEPATH = "backup.json";

	private ArrayList<City> cities = new ArrayList<>();

	private PerceptronTraveller pty = new PerceptronYoungTraveller();
	private PerceptronTraveller ptm = new PerceptronMiddleTraveller();
	private PerceptronTraveller pte = new PerceptronElderTraveller();
	private PerceptronTraveller ptc;

	private ArrayList<City> recommendedYoung = new ArrayList<>();
	private ArrayList<City> recommendedMiddle  = new ArrayList<>();
	private ArrayList<City> recommendedElder = new ArrayList<>();
	private ArrayList<City> recommendedCustom = new ArrayList<>();

	private JPanel contentPane;
	private JCheckBox chckbxCustomRecommendation;
	private JSeparator separator_1;

	private JTextField textField1;
	private JTextField textField2;
	private JTextField textField3;
	private JTextField textField4;
	private JTextField textField5;
	private JTextField textField6;
	private JTextField textField7;

	private JComboBox<String> comboBox1;
	private JComboBox<String> comboBox2;
	private JComboBox<String> comboBox3;
	private JComboBox<String> comboBox4;
	private JComboBox<String> comboBox5;
	private JComboBox<String> comboBox6;
	private JComboBox<String> comboBox7;

	private JLabel lblNewLabel_2;
	private JComboBox<String> comboBoxAgeRange;
	private JButton btnRecommend;
	private JLabel lblStatus;

	private JButton btnCovidInfo;
	private JTextField textFieldCityName;
	private JLabel lblNewLabel_3;
	private JButton btnAdd;
	private JButton btnRemove;
	private JButton btnClear;
	private JButton btnDateTable;

	private int createCitySemaphore = 0; //for creating cities threads
	private int retrieveDataSemaphore = 0; //for retrieving rest of city data from APIs	

	private double[] customTermsBias = new double[7];
	private String[] customTerms = new String[7];

	private CovidFrame cvframe;
	private File saveFile;
	private JComboBox<String> comboBoxSorting;

	private DateAdded dateFrame;  
	private JComboBox<String> countryComboBox;

	private static final Logger LOGGER = Logger.getLogger(ControllerGUI.class.getName());
	private JTable previewItems;
	private JTable recommendedItems;
	private JScrollPane scrollPane_1;
	private JScrollPane scrollPane;
	private JSeparator separator_1_1;
	private JSeparator separator;
	private TreeMap<String, String> hashMapSorted;	
	//private String[] sortedCountriesList;	
	private HashMap<String, String> countryCodesAndNamesLookUp;


	/**
	 * Create the frame.
	 * @throws UnsupportedLookAndFeelException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws ClassNotFoundException 
	 */
	public ControllerGUI() throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {	
		Handler fileHandler  = null;
		Handler consoleHandler  = null;
		createCountriesCodeHashMapLookUpTable();		

		hashMapSorted = makeHashMapSorted(hashMapSorted);

		try {
			//Creating consoleHandler and fileHandler
			consoleHandler = new ConsoleHandler();
			fileHandler  = new FileHandler("./log.txt");

			//Assigning handlers to LOGGER object
			LOGGER.addHandler(consoleHandler);
			LOGGER.addHandler(fileHandler);

			//Setting levels to handlers and LOGGER
			consoleHandler.setLevel(Level.OFF);
			fileHandler.setLevel(Level.ALL);
			LOGGER.setLevel(Level.ALL);

			LOGGER.config("Configuration done.");

			//Console handler removed
			LOGGER.removeHandler(consoleHandler);

		} catch (IOException exception) {
			LOGGER.log(Level.SEVERE, "Error occur in FileHandler.", exception);
		}

		drawGUI2();
		cvframe = new CovidFrame();
		LOGGER.log(Level.FINE, "Drew interface");


		LOGGER.log(Level.FINE, "Trying to read from file.");
		saveFile = new File(FILEPATH);
		if (saveFile.exists()) {
			try {
				cities = readJSON(saveFile);
				LOGGER.log(Level.FINE, "Succesfully read from file");
				displayPreviewItems();				
			} catch (IOException e) {				
				JOptionPane.showMessageDialog(null, "Could not read from file.", "Error", JOptionPane.ERROR_MESSAGE);
				LOGGER.log(Level.SEVERE, "Could not read from file. Reason given: " + e.getMessage());
			}
		}
	}

	/**
	 * Displays items from cities ArrayList in the preview JList
	 */
	private void displayPreviewItems() {
		Object[][] data = new Object[cities.size()][2];	
		for (int i = 0; i < cities.size(); i++) {
			data[i][0] = cities.get(i).getCityName();
			data[i][1] = countryCodesAndNamesLookUp.get(cities.get(i).getCountryCode());
		}
		DefaultTableModel table = new DefaultTableModel(data, new String[] {"City", "Country"});
		previewItems.setModel(table);
	}

	/**
	 * MouseListener interface method implementation
	 * @param MouseEvent
	 */
	@Override
	public void mouseClicked(MouseEvent e) {		
		if (e.getSource() == btnRecommend) {
			LOGGER.log(Level.FINE, "Clicked Recommend button.");
			if (cities.isEmpty()) {
				JOptionPane.showMessageDialog(null, "You must add atleast one city to get recommendation.", "No city added", JOptionPane.WARNING_MESSAGE);
				LOGGER.log(Level.WARNING, "Clicked Recommend button without adding cities.");	
			} else if (chckbxCustomRecommendation.isSelected() && allKeywordsAreNotFilled()) {
				JOptionPane.showMessageDialog(null, "You must fill all interests.", "Interest fields are empty", JOptionPane.WARNING_MESSAGE);			
			} else {			
				for (City city: cities) {					
					if (chckbxCustomRecommendation.isSelected() && !(city.getDataSource() == 2)) {
						LOGGER.log(Level.FINE, "Retrieving data for cities.");						
						retrieveDataSemaphoreUp();						
						retrieveCityData(city, this.customTerms, 2);					
					} else if (!chckbxCustomRecommendation.isSelected() && !(city.getDataSource() == 1)) {
						LOGGER.log(Level.FINE, "Retrieving data for cities with custom data.");						
						retrieveDataSemaphoreUp();						
						retrieveCityData(city, DEFAULT_TERMS, 1);
					}
				}

				//STILL TESTING
				if (retrieveDataSemaphore == 0) {
					makeRecommendations();
					sortRecommendations(new ScoreCompare().reversed());
					displayResults();		
				}
			}
		}


		if (e.getSource() == btnRemove) {
			LOGGER.log(Level.FINE, "Clicked Remove button.");
			int index = previewItems.getSelectedRow();
			if (index == -1) {
				LOGGER.log(Level.WARNING, "Clicked Remove button without selecting a city first.");
				JOptionPane.showMessageDialog(null, "You must select a city first.", "No city selected", JOptionPane.WARNING_MESSAGE);
			} else {
				cities.remove(index);
				LOGGER.log(Level.FINE, "Removed city.");
				displayPreviewItems();
				displayResults();
			}
		}


		if (e.getSource() == btnAdd) {
			LOGGER.log(Level.FINE, "Clicked Add button.");
			createCitySemaphoreUp();			

			addCity(textFieldCityName.getText(), hashMapSorted.get(countryComboBox.getSelectedItem().toString()));
			textFieldCityName.setText("");
			countryComboBox.setSelectedIndex(0);
			textFieldCityName.requestFocus();
		} 

		if (e.getSource() == btnClear) {
			LOGGER.log(Level.FINE, "Clicked Clear button.");

			recommendedYoung = new ArrayList<>();
			recommendedMiddle = new ArrayList<>();
			recommendedElder = new ArrayList<>();
			recommendedCustom = new ArrayList<>();
			cities = new ArrayList<>();

			emptyCustomizedTextfields();
						
			setDefaultFeaturesInTextfields();
			setDefaultComboBoxImportance();
		
			textFieldCityName.setText("");
			countryComboBox.setSelectedIndex(0);

			displayResults();
			displayPreviewItems();
		}

		if (e.getSource() == btnDateTable) {
			LOGGER.log(Level.FINE, "Clicked Date button.");
			HashMap<String, ArrayList<String>> hashtable = new HashMap<>();
			makeHashMap(cities, hashtable);			
			Object[][] data = new Object[cities.size()][2];			

			int i=0;
			for (Map.Entry<String, ArrayList<String>> entry: hashtable.entrySet()) {
				data[i][0] = entry.getKey();
				String str = String.join(", ", entry.getValue());
				data[i][1] = str;
				i++;
			}			

			dateFrame = new DateAdded(data);
			dateFrame.setVisible(true);
		}

		if (e.getSource() == btnCovidInfo) {
			LOGGER.log(Level.FINE, "Clicked COVID info button.");
			int index = recommendedItems.getSelectedRow();

			if (index == -1) {
				JOptionPane.showMessageDialog(null, "You must select a city first.", "No city selected", JOptionPane.WARNING_MESSAGE);
				LOGGER.log(Level.WARNING, "Clicked COVID info button without selecting cities.");
			} else {
				cvframe.setTextAreaText("");
				City city = cities.get(index);				
				cvframe.setTitle("COVID19 information for " + city.getCityName() + ", " + city.getCountryCode());
				setCovidData(city);
				cvframe.setVisible(true);
			}			
		}


		if (e.getSource() == chckbxCustomRecommendation) {
			if (chckbxCustomRecommendation.isSelected()) {
				LOGGER.log(Level.FINE, "Checked custom recommendations checkbox.");		
				customFeaturesEnable();
				restoreUserSelectedFeaturesInTextfields();
				restoreUserSelectedImportanceSettings();				
			} else {
				LOGGER.log(Level.FINE, "Unchecked custom recommendations checkbox.");				
				getCustomData();				
				customFeaturesDisable();
				setDefaultFeaturesInTextfields();
				setDefaultComboBoxImportance();
			}
		}
	}



	private boolean allKeywordsAreNotFilled() {
		if (textField1.getText().equals("") || textField2.getText().equals("") || textField3.getText().equals("") ||
			textField4.getText().equals("") || textField5.getText().equals("") || textField6.getText().equals("") || 
			textField7.getText().equals("")) {
			return true;
		}
		return false;
	}

	private void restoreUserSelectedImportanceSettings() {
		comboBox1.setSelectedIndex(weightsToImportanceLevel(0));
		comboBox2.setSelectedIndex(weightsToImportanceLevel(1));
		comboBox3.setSelectedIndex(weightsToImportanceLevel(2));
		comboBox4.setSelectedIndex(weightsToImportanceLevel(3));
		comboBox5.setSelectedIndex(weightsToImportanceLevel(4));
		comboBox6.setSelectedIndex(weightsToImportanceLevel(5));
		comboBox7.setSelectedIndex(weightsToImportanceLevel(6));

	}

	private int weightsToImportanceLevel(int index) {		
		if (customTermsBias[index] == 1) {
			return 0; 
		} else if (customTermsBias[index] == 0.5){
			return 1;
		} else if (customTermsBias[index] == 0) {
			return  2;
		} else if (customTermsBias[index] == -0.5) {
			return  3;
		} else {
			return  4;
		}	

	}

	private void restoreUserSelectedFeaturesInTextfields() {	
		textField1.setText(customTerms[0]);
		textField2.setText(customTerms[1]);
		textField3.setText(customTerms[2]);
		textField4.setText(customTerms[3]);
		textField5.setText(customTerms[4]);
		textField6.setText(customTerms[5]);
		textField7.setText(customTerms[6]);
	}

	/**
	 * It calls all data APIs for a given City object using a thread with SwingWorker 
	 * @param City object
	 * @param A String[] array of the keywords for the terms
	 * @param An integer for the dataSource used (1 or 2)
	 */
	private void retrieveCityData(City city, String[] terms, int dataSource) {
		LOGGER.log(Level.FINE, "Started retrieve data thread.");
		SwingWorker<Void, Void> worker = new SwingWorker<>() {
			@Override
			protected Void doInBackground() throws Exception {
				city.setTerms(terms);
				city.retrieveFeatureScore();							
				//city.retrieveCovidData();
				city.setDataSource(dataSource);				
				return null;
			}
			protected void done() {				
				retrieveDataSemaphoreDown();
			}
		};
		worker.execute();
	}

	/**
	 * A semaphore UP for the retrieveCityData thread. It increases the retrieveDataSemaphore variable by 1
	 * and also disables the recommend button and sets status label accordingly.
	 */
	private synchronized void retrieveDataSemaphoreUp() {
		this.retrieveDataSemaphore++;
		System.out.println("Semaphore is now " + retrieveDataSemaphore);
		this.btnRecommend.setEnabled(false);	
		this.lblStatus.setText("Please wait, working...");
	}

	/**
	 * A semaphore DOWN for the retrieveCityData. It checks if retrieveDataSemaphore variable is 0 
	 * and if is, it calls the appropriate post data retrieve methods. It also re-enables the recommend
	 * button and sets the status label to "Ready".	
	 */
	private synchronized void retrieveDataSemaphoreDown() {
		this.retrieveDataSemaphore--; //if retrieve data threads have finished run the recommendation perceptrons		
		System.out.println("Semaphore is now " + retrieveDataSemaphore);
		if (retrieveDataSemaphore == 0) {
			makeRecommendations();
			sortRecommendations(new ScoreCompare().reversed());
			displayResults();
		}
		this.btnRecommend.setEnabled(true);
		this.lblStatus.setText("Ready");
	}

	/**
	 * Sorts the recommended cities ArrayLists according to a Comparator given. 
	 * @param A comparator
	 */
	private void sortRecommendations(Comparator<City> sorter) {		
		Collections.sort(recommendedYoung, sorter);
		Collections.sort(recommendedMiddle, sorter);	
		Collections.sort(recommendedElder, sorter);	
		Collections.sort(recommendedCustom, sorter);	
	}


	/**
	 * Adds the appropriate recommended ArrayList results to the model of the recommended JTable in the GUI
	 * according to the perceptron selected. 
	 */	
	private void displayResults() {
		if (chckbxCustomRecommendation.isSelected()) {		
			Object[][] data = new Object[recommendedCustom.size()][2];	
			for (int i = 0; i < recommendedCustom.size(); i++) {
				data[i][0] = recommendedCustom.get(i).getCityName();
				data[i][1] = countryCodesAndNamesLookUp.get(recommendedCustom.get(i).getCountryCode());
			}
			DefaultTableModel table = new DefaultTableModel(data, new String[] {"City", "Country"});
			recommendedItems.setModel(table);
		} else {
			if (comboBoxAgeRange.getSelectedIndex() == 0) {				
				Object[][] data = new Object[recommendedYoung.size()][2];	
				for (int i = 0; i < recommendedYoung.size(); i++) {
					data[i][0] = recommendedYoung.get(i).getCityName();
					data[i][1] = countryCodesAndNamesLookUp.get(recommendedYoung.get(i).getCountryCode()); 					
				}
				DefaultTableModel table = new DefaultTableModel(data, new String[] {"City", "Country"});
				recommendedItems.setModel(table);
			} else if (comboBoxAgeRange.getSelectedIndex() == 1) {				
				Object[][] data = new Object[recommendedMiddle.size()][2];	
				for (int i = 0; i < recommendedMiddle.size(); i++) {
					data[i][0] = recommendedMiddle.get(i).getCityName();
					data[i][1] = countryCodesAndNamesLookUp.get(recommendedMiddle.get(i).getCountryCode());
				}
				DefaultTableModel table = new DefaultTableModel(data, new String[] {"City", "Country"});
				recommendedItems.setModel(table);
			} else {			
				Object[][] data = new Object[recommendedElder.size()][2];	
				for (int i = 0; i < recommendedElder.size(); i++) {
					data[i][0] = recommendedElder.get(i).getCityName();
					data[i][1] = countryCodesAndNamesLookUp.get(recommendedElder.get(i).getCountryCode());
				}
				DefaultTableModel table = new DefaultTableModel(data, new String[] {"City", "Country"});
				recommendedItems.setModel(table);
			}
		}

		for(City city: cities) {
			System.out.println(city.getCityName() + " data source: " + city.getDataSource());
			for (int i =0; i < city.getVectorRepresentation().length; i++) {
				System.out.printf("%.2f, ", city.getVectorRepresentation()[i]);

			}
			System.out.println();
		}

	}


	/**
	 * Creates PerceptronTraveller objects and calls their recommend methods
	 * for all Preceptrons.
	 */
	private void makeRecommendations() {
		if (chckbxCustomRecommendation.isSelected()) {
			getCustomData();
			ptc = new PerceptronCustomTraveller(this.customTermsBias);
			recommendedCustom = ptc.recommend(cities);
		} else {
			pty = new PerceptronYoungTraveller();
			recommendedYoung = pty.recommend(cities);

			ptm = new PerceptronMiddleTraveller();
			recommendedMiddle = ptm.recommend(cities);

			pte = new PerceptronElderTraveller();
			recommendedElder = pte.recommend(cities);
		}
	}



	/**
	 * ActionListener interface method implementation.
	 * @param ActionEvent
	 */
	@Override
	public void actionPerformed(ActionEvent e) {		
		if (e.getSource() == comboBoxAgeRange && !chckbxCustomRecommendation.isSelected()) {
			setDefaultComboBoxImportance();
		}

		if (e.getSource() == comboBoxSorting) {
			if (comboBoxSorting.getSelectedIndex() == 0) {
				sortRecommendations(new ScoreCompare());				
			} else if (comboBoxSorting.getSelectedIndex() == 1) {
				sortRecommendations(new ScoreCompare().reversed());				
			} else if (comboBoxSorting.getSelectedIndex() == 2) {
				sortRecommendations(new GeodesicCompare());				
			} else if (comboBoxSorting.getSelectedIndex() == 3) {
				sortRecommendations(new GeodesicCompare().reversed());				
			} else if (comboBoxSorting.getSelectedIndex() == 4) {
				sortRecommendations(new TimestampCompare());				
			} else if (comboBoxSorting.getSelectedIndex() == 5) {
				sortRecommendations(new GeodesicCompare().reversed());
			}
			displayResults();			
		}		
	}

	private void emptyCustomizedTextfields() {		
		textField1.setText("");
		textField2.setText("");
		textField3.setText("");
		textField4.setText("");
		textField5.setText("");
		textField6.setText("");
		textField7.setText("");		
	}


	public void customFeaturesDisable() {		
		textField1.setEnabled(false);
		textField2.setEnabled(false);
		textField3.setEnabled(false);
		textField4.setEnabled(false);
		textField5.setEnabled(false);
		textField6.setEnabled(false);
		textField7.setEnabled(false);
		comboBox1.setEnabled(false);
		comboBox2.setEnabled(false);
		comboBox3.setEnabled(false);
		comboBox4.setEnabled(false);
		comboBox5.setEnabled(false);
		comboBox6.setEnabled(false);
		comboBox7.setEnabled(false);
	}

	public void customFeaturesEnable() {
		textField1.setEnabled(true);
		textField2.setEnabled(true);
		textField3.setEnabled(true);
		textField4.setEnabled(true);
		textField5.setEnabled(true);
		textField6.setEnabled(true);
		textField7.setEnabled(true);
		comboBox1.setEnabled(true);
		comboBox2.setEnabled(true);
		comboBox3.setEnabled(true);
		comboBox4.setEnabled(true);
		comboBox5.setEnabled(true);
		comboBox6.setEnabled(true);
		comboBox7.setEnabled(true);
	}

	/**
	 * Reads the custom data given by a user from the GUI.
	 */
	private void getCustomData() {	
		int[] selections = new int[7];

		customTerms[0] = textField1.getText();		
		customTerms[1] = textField2.getText();
		customTerms[2] = textField3.getText();
		customTerms[3] = textField4.getText();
		customTerms[4] = textField5.getText();
		customTerms[5] = textField6.getText();
		customTerms[6] = textField7.getText();

		selections[0] = comboBox1.getSelectedIndex();
		selections[1] = comboBox2.getSelectedIndex();
		selections[2] = comboBox3.getSelectedIndex();
		selections[3] = comboBox4.getSelectedIndex();
		selections[4] = comboBox5.getSelectedIndex();
		selections[5] = comboBox6.getSelectedIndex();
		selections[6] = comboBox7.getSelectedIndex();

		for (int i = 0; i<selections.length; i++) {
			if (selections[i] == 0) {
				this.customTermsBias[i] = 1; 
			} else if (selections[i] == 1){
				this.customTermsBias[i] = 0.5;
			} else if (selections[i] == 2) {
				this.customTermsBias[i] = 0;
			} else if (selections[i] == 3) {
				this.customTermsBias[i] = -0.5;
			} else if (selections[i] == 4) {
				this.customTermsBias[i] = -1;
			}
		}
	}


	/**
	 * Creates a new city object and calls the open weather API, using threads
	 * with SwingWorker.
	 * @param A string with a city name.
	 * @param A string with a country code using ISO 3166 format.
	 */
	private synchronized void addCity(String cityName, String countryCode) {
		LOGGER.log(Level.FINE, "Started add city thread.");
		SwingWorker<Void, Void> worker = new SwingWorker<>() {

			@Override
			protected Void doInBackground() throws Exception {
				Date date = new Date();
				City temp;

				try {					
					temp = new City(cityName, countryCode, DEFAULT_TERMS, date.getTime());
					if (!cities.contains(temp)) {
						cities.add(temp);
					} else {						
						JOptionPane.showMessageDialog(null, "The city " + temp.getCityName() + ", " + temp.getCountryCode()  + " has already been added.", "Dublicate city found", JOptionPane.WARNING_MESSAGE);		
					}

				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}			
				return null;
			}

			@Override
			protected void done() {				
				displayPreviewItems();
				createCitySemaphoreDown();
			}			
		};
		worker.execute();


	}

	/**
	 * A semaphore UP for the createCity process.
	 * It also disables the recommend button and sets 
	 * the appropriate message in the status label.
	 */
	private synchronized void createCitySemaphoreUp() {
		this.createCitySemaphore++;
		this.btnRecommend.setEnabled(false);
		this.lblStatus.setText("Please wait, working...");
	}

	/**
	 * A semaphore DOWN for the createCity process.
	 * It also re-enables the recommend button and sets
	 * the appropriate status label message.
	 */
	private synchronized void createCitySemaphoreDown() {
		this.createCitySemaphore--;
		if (this.createCitySemaphore == 0) {
			this.btnRecommend.setEnabled(true);
			this.lblStatus.setText("Ready");
		}
	}

	/*
	private City cityDistance(PerceptronTraveller pTrv) throws IndexOutOfBoundsException {
		int index =0;
		double Min = 1;
		for (int i = 0; i <  pTrv.getRecCities().size(); i++) {
			if (pTrv.getRecCities().get(i).getVectorRepresentation()[9] < Min) {
				Min =  pTrv.getRecCities().get(i).getVectorRepresentation()[9];
				index = i;
			}
		}
		return pTrv.getRecCities().get(index);
	}*/

	/**
	 * This method adds ArrayList of city objects into a HashMap, with date when city was first added
	 * as the key and with the name of the city as the value
	 * 
	 * @author it22165
	 * @since 05/12/2021
	 * @param cities is the ArrayList of cities to be added to HashMap
	 * @param citiesHashMap is the HashMap where cities are added
	 */
	private void makeHashMap(ArrayList<City> cities, HashMap<String, ArrayList<String>> citiesHashMap) {
		SimpleDateFormat date = new SimpleDateFormat("EEEE");
		for (City city : cities) {
			String key = date.format(city.getTimestamp());
			if (!citiesHashMap.containsKey(key)) {
				citiesHashMap.put(key, new ArrayList<String>());
			}
			citiesHashMap.get(key).add(city.getCityName());		
		}
	}

	private String[] getAllCountries() {//consider argument String[] array
		//Create String array to save countries' names from list of countries created with Locale
		String[] countries = new String[Locale.getISOCountries().length+1];
		//Create list of all countries (initials-code and name) defined in ISO 3166 with Locale
		String[] localeCountries = Locale.getISOCountries();
		
		countries[0] = "-";
		for (int i = 1; i <= localeCountries.length; i++) {
			Locale obj = new Locale("", localeCountries[i-1]);
			countries[i] = (obj.getDisplayCountry(Locale.ENGLISH) + ", " + obj.getCountry());
		}
		Arrays.sort(countries);		
		return countries;
	}

	
	private void createCountriesCodeHashMapLookUpTable() {		
		String[] CountryISOList = Locale.getISOCountries();
		countryCodesAndNamesLookUp = new HashMap<>();
		ArrayList<String> temp = new ArrayList<>();
		
		temp.add("-");
		for (int i =0; i < CountryISOList.length; i++) {
			Locale locale = new Locale("", CountryISOList[i]);			
			countryCodesAndNamesLookUp.put(locale.getCountry(), locale.getDisplayCountry(Locale.ENGLISH));
			temp.add(locale.getDisplayCountry(Locale.ENGLISH) + ", " + locale.getCountry());
		}		
		Collections.sort(temp);		
		//sortedCountriesList = temp.toArray(sortedCountriesList);		
		/*
		for (Entry<String, String> entry: countryCodesAndNamesLookUp.entrySet()) {
			System.out.println(entry.getKey() + " " + entry.getValue());			
		}*/			
	}	
	

	private TreeMap<String, String> makeHashMapSorted(TreeMap<String, String> inputHashMap) {
		//create HashMap of countries and codes with Locale
		HashMap<String, String> countriesNameCode = new HashMap<>();
		for (String iso : Locale.getISOCountries()) {
			Locale l = new Locale("", iso);
			countriesNameCode.put((l.getDisplayCountry(Locale.ENGLISH) + ", " + l.getCountry()), iso);
		}
		//sort HashMap with TreeMap
		TreeMap<String, String> hashMapSorted = new TreeMap<>(countriesNameCode);
		return (inputHashMap = hashMapSorted);//????
	}


	/**
	 * Serialization into JSON file (write method). This method converts ArrayList of cities objects into strings
	 * and saves them in a Json file. 
	 * 
	 * @author it22165
	 * @since 05/12/2021
	 * @param cities is the ArrayList of cities created
	 * @param filename is the name of the saved json file that keeps representation of cities objects into strings 
	 * @throws JsonGenerationException
	 * @throws JsonMappingException
	 * @throws IOException 
	 */
	private void writeJSON(ArrayList<City> cities, File filename) throws JsonGenerationException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		mapper.writeValue(filename, cities);
	}


	/**
	 * Deserialization from JSON file (read method). This method gets back ArrayList of cities objects from saved json file,
	 * where these objects are represented as strings.
	 * 
	 * @author it22165
	 * @since 05/12/2021
	 * @param filename is the name of the saved json file that keeps representation of cities objects into strings 
	 * @return the ArrayList cities as objects
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException 
	 */
	private ArrayList<City> readJSON(File filename) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		ArrayList<City> cities = mapper.readValue(filename, new TypeReference<ArrayList<City>>(){});		
		return cities;    
	}

	public void setDefaultFeaturesInTextfields() {        
		textField1.setText(DEFAULT_TERMS[0]);
		textField2.setText(DEFAULT_TERMS[1]);
		textField3.setText(DEFAULT_TERMS[2]);
		textField4.setText(DEFAULT_TERMS[3]);
		textField5.setText(DEFAULT_TERMS[4]);
		textField6.setText(DEFAULT_TERMS[5]);
		textField7.setText(DEFAULT_TERMS[6]);
	}

	private void setDefaultComboBoxImportance() {
		comboBox1.setSelectedIndex(setCategoryAccordingToWeights(0));
		comboBox2.setSelectedIndex(setCategoryAccordingToWeights(1));
		comboBox3.setSelectedIndex(setCategoryAccordingToWeights(2));
		comboBox4.setSelectedIndex(setCategoryAccordingToWeights(3));
		comboBox5.setSelectedIndex(setCategoryAccordingToWeights(4));
		comboBox6.setSelectedIndex(setCategoryAccordingToWeights(5));
		comboBox7.setSelectedIndex(setCategoryAccordingToWeights(6));
	}

	
	/**
	 * Convert weight range to one of 5 importance option in the combo boxes
	 * @param index
	 * @return
	 */
	private int setCategoryAccordingToWeights(int index) {
		double[] weights;
		if (comboBoxAgeRange.getSelectedIndex() == 0) {
			weights = pty.getWeightBias();
		} else if (comboBoxAgeRange.getSelectedIndex() == 2) {
			weights = ptm.getWeightBias();
		} else {
			weights = pte.getWeightBias();
		}

		int category = 2;

		if (weights[index] > -1.0) {
			category = 4;
		} 
		if (weights[index] > -1.0) {
			category = 3;
		}
		if (weights[index] > -0.5) {
			category = 2;
		}
		if (weights[index] > 0) {
			category = 1;
		}
		if (weights[index] > 0.5) {
			category = 0;
		}
		return category;

	}

	/**
	 * A WindowListener interface method implementation.
	 * @param A WindowEvent.
	 */
	@Override
	public void windowClosing(WindowEvent e) {
		LOGGER.log(Level.FINE, "Clicked close button.");
		try {
			writeJSON(cities, saveFile);
			LOGGER.log(Level.FINE, "Saved data.");
		} catch (IOException e1) {
			LOGGER.log(Level.SEVERE, "Could not write to file. Reason given: " + e1.getMessage());
			JOptionPane.showMessageDialog(null, "Could not write to file.", "Error", JOptionPane.ERROR_MESSAGE);
			e1.printStackTrace();
		}
		System.exit(0);		
	}



	/**
	 * I builds the COVID info string from the COVID object Jackson object.
	 * @param A City object.
	 */
	private void setCovidData(City city) {		
		List<String> infoArrayList = new ArrayList<>();		

		infoArrayList.add("COVID Risk level: " + city.getCovidData().getData().getDiseaseRiskLevel() + "\n");
		infoArrayList.add("Last report: " + city.getCovidData().getData().getDiseaseCases().getDate() + "\n");		

		infoArrayList.add("Active cases: " + city.getCovidData().getData().getDiseaseCases().getActive() + "\n");
		infoArrayList.add("Confirmed cases: " + city.getCovidData().getData().getDiseaseCases().getConfirmed() + "\n");
		infoArrayList.add("Deaths: " + city.getCovidData().getData().getDiseaseCases().getDeaths() + "\n\n");

		infoArrayList.add("INFECTION DATA\n");
		infoArrayList.add("Last report: " + city.getCovidData().getData().getDiseaseInfection().getDate() +"\n");
		infoArrayList.add("Map Link: " + city.getCovidData().getData().getDiseaseInfection().getInfectionMapLink() +"\n");		
		infoArrayList.add("Infection level: " + city.getCovidData().getData().getDiseaseInfection().getLevel() +"\n");
		infoArrayList.add("Infection rate: " + city.getCovidData().getData().getDiseaseInfection().getRate() +"\n\n");

		infoArrayList.add("AREA ACCESS RESTRICTION -- DISEASE TESTING\n");
		infoArrayList.add("Last report: " + city.getCovidData().getData().getAreaAccessRestriction().getDiseaseTesting().getDate() + "\n");
		infoArrayList.add("Is testing required: " + city.getCovidData().getData().getAreaAccessRestriction().getDiseaseTesting().getIsRequired() + "\n");
		infoArrayList.add("Testing type: " + city.getCovidData().getData().getAreaAccessRestriction().getDiseaseTesting().getTestType()+ "\n");
		infoArrayList.add("Testing rules: " + city.getCovidData().getData().getAreaAccessRestriction().getDiseaseTesting().getRules() + "\n");
		infoArrayList.add("Misc info: " + city.getCovidData().getData().getAreaAccessRestriction().getDiseaseTesting().getText() + "\n");
		infoArrayList.add("Testing should be done: " + city.getCovidData().getData().getAreaAccessRestriction().getDiseaseTesting().getWhen() + "\n");
		infoArrayList.add("Minimum age required for testing: " + city.getCovidData().getData().getAreaAccessRestriction().getDiseaseTesting().getMinimumAge()+ "\n");
		infoArrayList.add("Validity period, delay: " + city.getCovidData().getData().getAreaAccessRestriction().getDiseaseTesting().getValidityPeriod().getDelay()+ "\n");		
		infoArrayList.add("Validity period, reference date type: " + city.getCovidData().getData().getAreaAccessRestriction().getDiseaseTesting().getValidityPeriod().getReferenceDateType()+ "\n\n");

		infoArrayList.add("AREA ACCESS RESTRICTION -- DISEASE VACCINATION\n");
		infoArrayList.add("Last report: " + city.getCovidData().getData().getAreaAccessRestriction().getDiseaseVaccination().getDate() +"\n");
		infoArrayList.add("Details: " + city.getCovidData().getData().getAreaAccessRestriction().getDiseaseVaccination().getDetails() +"\n");
		infoArrayList.add("Excemptions: " + city.getCovidData().getData().getAreaAccessRestriction().getDiseaseVaccination().getExemptions() +"\n");
		infoArrayList.add("Is required: " + city.getCovidData().getData().getAreaAccessRestriction().getDiseaseVaccination().getIsRequired() +"\n");
		infoArrayList.add("Vaccination Policy: " + city.getCovidData().getData().getAreaAccessRestriction().getDiseaseVaccination().getPolicy() +"\n");
		infoArrayList.add("Reference link: " + city.getCovidData().getData().getAreaAccessRestriction().getDiseaseVaccination().getReferenceLink() +"\n");
		infoArrayList.add("Misc info: " + city.getCovidData().getData().getAreaAccessRestriction().getDiseaseVaccination().getText() +"\n");
		infoArrayList.add("Accepted certificates: " + city.getCovidData().getData().getAreaAccessRestriction().getDiseaseVaccination().getAcceptedCertificates() +"\n");
		infoArrayList.add("Additional properties: " + city.getCovidData().getData().getAreaAccessRestriction().getDiseaseVaccination().getAdditionalProperties()+"\n");
		infoArrayList.add("Qualified vaccines: " + city.getCovidData().getData().getAreaAccessRestriction().getDiseaseVaccination().getQualifiedVaccines()+"\n\n");

		infoArrayList.add("AREA ACCESS RESTRICTION -- ENTRY DETAILS\n");
		infoArrayList.add("Entry Ban: " + city.getCovidData().getData().getAreaAccessRestriction().getEntry().getBan()+"\n");
		infoArrayList.add("Last report: " + city.getCovidData().getData().getAreaAccessRestriction().getEntry().getDate()+"\n");
		infoArrayList.add("Exemptions: " + city.getCovidData().getData().getAreaAccessRestriction().getEntry().getExemptions()+"\n");
		infoArrayList.add("Rules: " + city.getCovidData().getData().getAreaAccessRestriction().getEntry().getRules()+"\n");
		infoArrayList.add("Misc info: " + city.getCovidData().getData().getAreaAccessRestriction().getEntry().getText()+"\n");
		infoArrayList.add("Through date: " + city.getCovidData().getData().getAreaAccessRestriction().getEntry().getThroughDate()+"\n");		

		//infoarray.add("Banned areas: " + city.getCovidData().getData().getAreaAccessRestriction().getEntry().getBannedArea()+"\n");
		infoArrayList.add("Banned areas: ");
		if (city.getCovidData().getData().getAreaAccessRestriction().getEntry().getBannedArea() != null) {
			for (int i = 0; i < city.getCovidData().getData().getAreaAccessRestriction().getEntry().getBannedArea().size(); i++) {			
				infoArrayList.add(city.getCovidData().getData().getAreaAccessRestriction().getEntry().getBannedArea().get(i).getName());
				infoArrayList.add(" ");
				infoArrayList.add(city.getCovidData().getData().getAreaAccessRestriction().getEntry().getBannedArea().get(i).getIataCode());
				//infoarray.add(" ";
				//infoarray.add(city.getCovidData().getData().getAreaAccessRestriction().getEntry().getBannedArea().get(i).getAreaType();
				infoArrayList.add(", ");
			}
		}
		infoArrayList.add("\n");
		infoArrayList.add("Border ban: ");
		//infoarray.add("Border ban: " + city.getCovidData().getData().getAreaAccessRestriction().getEntry().getBorderBan()+"\n");

		if (city.getCovidData().getData().getAreaAccessRestriction().getEntry().getBorderBan() != null) {
			for (int i = 0; i < city.getCovidData().getData().getAreaAccessRestriction().getEntry().getBorderBan().size(); i++) {			
				infoArrayList.add(city.getCovidData().getData().getAreaAccessRestriction().getEntry().getBorderBan().get(i).getBorderType());
				infoArrayList.add(" ");
				infoArrayList.add(city.getCovidData().getData().getAreaAccessRestriction().getEntry().getBorderBan().get(i).getStatus());
				//infoarray.add(" ";
				//infoarray.add(city.getCovidData().getData().getAreaAccessRestriction().getEntry().getBannedArea().get(i).getAreaType();
				infoArrayList.add(", ");
			}
		}
		infoArrayList.add("\n\n");

		infoArrayList.add("AREA ACCESS RESTRICTION -- EXIT DETAILS\n");
		infoArrayList.add("Last report: " + city.getCovidData().getData().getAreaAccessRestriction().getExit().getDate() +"\n");
		infoArrayList.add("Is banned: " + city.getCovidData().getData().getAreaAccessRestriction().getExit().getIsBanned() +"\n");
		infoArrayList.add("Rules link: " + city.getCovidData().getData().getAreaAccessRestriction().getExit().getRulesLink() +"\n");
		infoArrayList.add("Special requirements: " + city.getCovidData().getData().getAreaAccessRestriction().getExit().getSpecialRequirements() +"\n");
		infoArrayList.add("Misc details: " + city.getCovidData().getData().getAreaAccessRestriction().getExit().getText() +"\n\n");

		infoArrayList.add("AREA ACCESS RESTRICTION -- MASK\n");
		infoArrayList.add("Last report: " + city.getCovidData().getData().getAreaAccessRestriction().getMask().getDate() +"\n");
		infoArrayList.add("Is required: " + city.getCovidData().getData().getAreaAccessRestriction().getMask().getIsRequired() +"\n");
		infoArrayList.add("Misc info: " + city.getCovidData().getData().getAreaAccessRestriction().getMask().getText() +"\n\n");


		//cvframe.appendTextAreaText("Area access restriction Other restriction Date: " + city.getCovidData().getData().getAreaAccessRestriction().getOtherRestriction().getDate()+"\n\n");
		infoArrayList.add("AREA ACCESS RESTRICTION -- QUARANTINE MODALITY\n");
		infoArrayList.add("Last report: " + city.getCovidData().getData().getAreaAccessRestriction().getQuarantineModality().getDate()+"\n");
		infoArrayList.add("Elligible person: " + city.getCovidData().getData().getAreaAccessRestriction().getQuarantineModality().getEligiblePerson()+"\n");
		infoArrayList.add("Mandate list: " + city.getCovidData().getData().getAreaAccessRestriction().getQuarantineModality().getMandateList()+"\n");
		infoArrayList.add("Quarantine type: " + city.getCovidData().getData().getAreaAccessRestriction().getQuarantineModality().getQuarantineType()+"\n");
		infoArrayList.add("Rules: " + city.getCovidData().getData().getAreaAccessRestriction().getQuarantineModality().getRules()+"\n");
		infoArrayList.add("Misc info: " + city.getCovidData().getData().getAreaAccessRestriction().getQuarantineModality().getText()+"\n");
		infoArrayList.add("Duration: " + city.getCovidData().getData().getAreaAccessRestriction().getQuarantineModality().getDuration()+"\n");
		//infoarray.add("Quarantine on arrival areas: " + city.getCovidData().getData().getAreaAccessRestriction().getQuarantineModality().getQuarantineOnArrivalAreas()+"\n\n";
		infoArrayList.add("Quarantine on arrival areas: ");	
		if (city.getCovidData().getData().getAreaAccessRestriction().getQuarantineModality().getQuarantineOnArrivalAreas() != null) {
			for (int i = 0; i < city.getCovidData().getData().getAreaAccessRestriction().getQuarantineModality().getQuarantineOnArrivalAreas().size(); i++) {			
				infoArrayList.add(city.getCovidData().getData().getAreaAccessRestriction().getQuarantineModality().getQuarantineOnArrivalAreas().get(i).getIataCode());
				//infoarray.add(" ";
				//infoarray.add(city.getCovidData().getData().getAreaAccessRestriction().getQuarantineModality().getQuarantineOnArrivalAreas().get(i).getAreaType();
				//infoarray.add(" ";
				//infoarray.add(city.getCovidData().getData().getAreaAccessRestriction().getQuarantineModality().getQuarantineOnArrivalAreas().get(i).getAdditionalProperties();
				infoArrayList.add(", ");
			}
		}

		infoArrayList.add("\n\n");

		infoArrayList.add("AREA ACCESS RESTRICTION -- APPS\n");
		infoArrayList.add("Last report: " + city.getCovidData().getData().getAreaAccessRestriction().getTracingApplication().getDate() +"\n");
		infoArrayList.add("Is required: " + city.getCovidData().getData().getAreaAccessRestriction().getTracingApplication().getIsRequired() +"\n");
		infoArrayList.add("Misc info: " + city.getCovidData().getData().getAreaAccessRestriction().getTracingApplication().getText() +"\n");
		infoArrayList.add("Website: " + city.getCovidData().getData().getAreaAccessRestriction().getTracingApplication().getWebsite() +"\n");
		infoArrayList.add("Android Url: " + city.getCovidData().getData().getAreaAccessRestriction().getTracingApplication().getAndroidUrl() +"\n");
		infoArrayList.add("Ios url: " + city.getCovidData().getData().getAreaAccessRestriction().getTracingApplication().getIosUrl() +"\n\n");

		infoArrayList.add("AREA ACCESS RESTRICTION -- TRANSPORTATION\n");
		infoArrayList.add("Last report: " + city.getCovidData().getData().getAreaAccessRestriction().getTransportation().getDate() +"\n");
		infoArrayList.add("Is banned: " + city.getCovidData().getData().getAreaAccessRestriction().getTransportation().getIsBanned() +"\n");
		infoArrayList.add("Misc info: " + city.getCovidData().getData().getAreaAccessRestriction().getTransportation().getText() +"\n");
		infoArrayList.add("Through date: " + city.getCovidData().getData().getAreaAccessRestriction().getTransportation().getThroughDate() +"\n");
		infoArrayList.add("Transportation type: " + city.getCovidData().getData().getAreaAccessRestriction().getTransportation().getTransportationType() +"\n\n");

		infoArrayList.add("AREA POLICY\n");
		infoArrayList.add("Last report: " + city.getCovidData().getData().getAreaPolicy().getDate() +"\n");
		infoArrayList.add("Start date: " + city.getCovidData().getData().getAreaPolicy().getStartDate() +"\n");
		infoArrayList.add("End date: " + city.getCovidData().getData().getAreaPolicy().getEndDate() +"\n");
		infoArrayList.add("Reference link: " + city.getCovidData().getData().getAreaPolicy().getReferenceLink() +"\n");
		infoArrayList.add("Status: " + city.getCovidData().getData().getAreaPolicy().getStatus() +"\n");
		infoArrayList.add("Misc info: " + city.getCovidData().getData().getAreaPolicy().getText()+"\n\n");


		//infoarray.add("Area restrictions: " + city.getCovidData().getData().getAreaRestrictions()+"\n\n";
		infoArrayList.add("AREA RESTRICTIONS:\n");
		if (city.getCovidData().getData().getAreaRestrictions() != null) {
			for (int i = 0; i <city.getCovidData().getData().getAreaRestrictions().size(); i++) {			
				infoArrayList.add(city.getCovidData().getData().getAreaRestrictions().get(i).getDate());
				infoArrayList.add("\n");
				infoArrayList.add(city.getCovidData().getData().getAreaRestrictions().get(i).getRestrictionType());
				infoArrayList.add(" ");
				infoArrayList.add(city.getCovidData().getData().getAreaRestrictions().get(i).getText());
				infoArrayList.add(" ");
				infoArrayList.add(city.getCovidData().getData().getAreaRestrictions().get(i).getTitle());
				infoArrayList.add(" ");
				infoArrayList.add("\n");
			}
		}

		infoArrayList.add("\n");

		//infoarray.add("Area vaccinated: " + city.getCovidData().getData().getAreaVaccinated() +"\n\n";
		infoArrayList.add("AREA VACCINATED:\n");
		/*
		if (city.getCovidData().getData().getAreaVaccinated() != null) {
			for (int i = 0; i <city.getCovidData().getData().getAreaVaccinated().size(); i++) {			
				infoArrayList.add(city.getCovidData().getData().getAreaVaccinated().get(i).getDate());
				infoArrayList.add("\n");

				infoArrayList.add(city.getCovidData().getData().getAreaVaccinated().get(i).getText());
				infoArrayList.add(" ");

				infoArrayList.add("\n");
			}
		}*/

		infoArrayList.add("\n");

		infoArrayList.add("Data sources covid dashboard link: " + city.getCovidData().getData().getDataSources().getCovidDashboardLink() +"\n");
		infoArrayList.add("Goverment site link: " + city.getCovidData().getData().getDataSources().getGovernmentSiteLink() +"\n");
		infoArrayList.add("Health department site link: " + city.getCovidData().getData().getDataSources().getHealthDepartementSiteLink() +"\n\n");			

		infoArrayList.removeAll(Collections.singleton(null)); //THIS FUCKING SHIT FUCK OFF BITCH		

		List<String> filteredElements = infoArrayList.stream().map(string -> string.replace("null", ""))
				.map(string -> string.replace("</p>", ""))
				.map(string -> string.replace("<p>", ""))
				.map(string -> string.replace("<strong>", ""))
				.map(string -> string.replace("</strong>", ""))
				.map(string -> string.replace("<a>", ""))
				.map(string -> string.replace("</a>", ""))
				.map(string -> string.replace("<span>", ""))
				.map(string -> string.replace("</span>", ""))
				.collect(Collectors.toList());

		String filteredStr = filteredElements.stream().filter(string -> !string.isEmpty()).collect(Collectors.joining());

		cvframe.appendTextAreaText(filteredStr);		
	}

	/**
	 * Draw interface components.
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws UnsupportedLookAndFeelException
	 */
	private void drawGUI2() throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
		setResizable(false);
		setTitle("City Recommend");
		setIconImage(Toolkit.getDefaultToolkit().getImage(ControllerGUI.class.getResource("/graphics/MainIcon.png")));
		UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		addWindowListener(this);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 848, 513);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		comboBoxAgeRange = new JComboBox<>();
		comboBoxAgeRange.setModel(new DefaultComboBoxModel<>(new String[] {"16-25", "26-60", "61++"}));
		comboBoxAgeRange.setToolTipText("Select your age group");
		comboBoxAgeRange.setBounds(344, 9, 72, 22);
		comboBoxAgeRange.addActionListener(this);
		contentPane.add(comboBoxAgeRange);

		JLabel labelAge = new JLabel("Age group");

		labelAge.setBounds(276, 12, 56, 16);
		contentPane.add(labelAge);

		textFieldCityName = new JTextField();

		textFieldCityName.setBounds(108, 9, 147, 22);
		contentPane.add(textFieldCityName);
		textFieldCityName.setColumns(10);

		JLabel labelChooseCity = new JLabel("City name");

		labelChooseCity.setBounds(42, 12, 54, 16);
		contentPane.add(labelChooseCity);

		countryComboBox = new JComboBox<>();

		countryComboBox.setBounds(108, 43, 147, 24);
		contentPane.add(countryComboBox);

		JLabel labelChooseCountry = new JLabel("Country");

		labelChooseCountry.setBounds(52, 46, 43, 16);
		contentPane.add(labelChooseCountry);

		btnAdd = new JButton("Add");
		btnAdd.addMouseListener(this);

		btnAdd.setBounds(151, 77, 104, 25);
		contentPane.add(btnAdd);

		btnRemove = new JButton("Remove");
		btnRemove.addMouseListener(this);

		btnRemove.setBounds(10, 409, 121, 25);
		contentPane.add(btnRemove);

		btnClear = new JButton("Clear");
		btnClear.addMouseListener(this);

		btnClear.setBounds(134, 409, 121, 25);
		contentPane.add(btnClear);

		btnRecommend = new JButton("Recommend");
		btnRecommend.setBounds(276, 43, 245, 25);
		btnRecommend.addMouseListener(this);
		contentPane.add(btnRecommend);

		scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 111, 245, 291);
		contentPane.add(scrollPane);	


		previewItems = new JTable();
		previewItems.setShowGrid(false);
		previewItems.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		previewItems.setModel(new DefaultTableModel(
				new Object[][] {
				},
				new String[] {
						"City", "Country"
				}
				));

		scrollPane.setViewportView(previewItems);
		lblStatus = new JLabel("Ready");

		lblStatus.setBounds(10, 452, 141, 15);
		contentPane.add(lblStatus);

		separator_1 = new JSeparator();
		separator_1.setOrientation(SwingConstants.VERTICAL);
		separator_1.setBounds(265, 0, 9, 444);
		contentPane.add(separator_1);

		comboBoxSorting = new JComboBox<>();
		comboBoxSorting.setModel(new DefaultComboBoxModel<String>(new String[] {"Sort by score | Ascending", "Sort by score | Descending", "Sort by distance | Ascending", "Sort by distance | Descending", "Sort by time added | Ascending", "Sort by time added | Descending"}));
		comboBoxSorting.addActionListener(this);
		comboBoxSorting.setBounds(276, 78, 245, 22);
		contentPane.add(comboBoxSorting);

		scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(276, 111, 245, 291);
		contentPane.add(scrollPane_1);


		recommendedItems = new JTable();
		recommendedItems.setShowGrid(false);
		recommendedItems.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		recommendedItems.setModel(new DefaultTableModel(
				new Object[][] {
				},
				new String[] {
						"City", "Country"
				}
				));

		scrollPane_1.setViewportView(recommendedItems);

		btnCovidInfo = new JButton("COVID Information");
		btnCovidInfo.addMouseListener(this);

		btnCovidInfo.setBounds(276, 409, 245, 25);
		contentPane.add(btnCovidInfo);

		separator_1_1 = new JSeparator();
		separator_1_1.setOrientation(SwingConstants.VERTICAL);
		separator_1_1.setBounds(531, 0, 9, 444);
		contentPane.add(separator_1_1);

		chckbxCustomRecommendation = new JCheckBox("Customize criteria");
		chckbxCustomRecommendation.setBounds(548, 77, 121, 25);
		chckbxCustomRecommendation.addMouseListener(this);
		contentPane.add(chckbxCustomRecommendation);

		JLabel lblNewLabel_1 = new JLabel("Feature 1");
		lblNewLabel_1.setBounds(554, 141, 48, 16);
		contentPane.add(lblNewLabel_1);

		JLabel lblNewLabel_1_1 = new JLabel("Feature 2");
		lblNewLabel_1_1.setBounds(554, 171, 48, 16);
		contentPane.add(lblNewLabel_1_1);

		JLabel lblNewLabel_1_2 = new JLabel("Feature 3");
		lblNewLabel_1_2.setBounds(554, 202, 48, 16);
		contentPane.add(lblNewLabel_1_2);

		JLabel lblNewLabel_1_3 = new JLabel("Feature 4");
		lblNewLabel_1_3.setBounds(554, 233, 48, 16);
		contentPane.add(lblNewLabel_1_3);

		JLabel lblNewLabel_1_4 = new JLabel("Feature 5");
		lblNewLabel_1_4.setBounds(554, 264, 48, 16);
		contentPane.add(lblNewLabel_1_4);

		JLabel lblNewLabel_1_5 = new JLabel("Feature 6");
		lblNewLabel_1_5.setBounds(554, 295, 48, 16);
		contentPane.add(lblNewLabel_1_5);

		JLabel lblNewLabel_1_6 = new JLabel("Feature 7");
		lblNewLabel_1_6.setBounds(554, 326, 48, 16);
		contentPane.add(lblNewLabel_1_6);

		textField1 = new JTextField();
		textField1.setBounds(614, 136, 116, 22);
		contentPane.add(textField1);
		textField1.setColumns(10);

		textField2 = new JTextField();
		textField2.setColumns(10);
		textField2.setBounds(614, 167, 116, 22);
		contentPane.add(textField2);

		textField3 = new JTextField();
		textField3.setColumns(10);
		textField3.setBounds(614, 198, 116, 22);
		contentPane.add(textField3);

		textField4 = new JTextField();
		textField4.setColumns(10);
		textField4.setBounds(614, 229, 116, 22);
		contentPane.add(textField4);

		textField5 = new JTextField();
		textField5.setColumns(10);
		textField5.setBounds(614, 260, 116, 22);
		contentPane.add(textField5);

		textField6 = new JTextField();
		textField6.setColumns(10);
		textField6.setBounds(614, 291, 116, 22);
		contentPane.add(textField6);

		textField7 = new JTextField();
		textField7.setColumns(10);
		textField7.setBounds(614, 322, 116, 22);
		contentPane.add(textField7);

		comboBox1 = new JComboBox<>();

		comboBox1.setModel(new DefaultComboBoxModel<>(new String[] {"Very high", "High", "Medium", "Low", "Very low"}));
		comboBox1.setBounds(742, 137, 78, 22);
		contentPane.add(comboBox1);

		comboBox2 = new JComboBox<>();

		comboBox2.setModel(new DefaultComboBoxModel<>(new String[] {"Very high", "High", "Medium", "Low", "Very low"}));
		comboBox2.setBounds(742, 167, 78, 22);
		contentPane.add(comboBox2);

		comboBox3 = new JComboBox<>();
		comboBox3.setModel(new DefaultComboBoxModel<>(new String[] {"Very high", "High", "Medium", "Low", "Very low"}));
		comboBox3.setBounds(742, 198, 78, 22);
		contentPane.add(comboBox3);

		comboBox4 = new JComboBox<>();
		comboBox4.setModel(new DefaultComboBoxModel<>(new String[] {"Very high", "High", "Medium", "Low", "Very low"}));
		comboBox4.setBounds(742, 229, 78, 22);
		contentPane.add(comboBox4);

		comboBox5 = new JComboBox<>();
		comboBox5.setModel(new DefaultComboBoxModel<>(new String[] {"Very high", "High", "Medium", "Low", "Very low"}));
		comboBox5.setBounds(742, 260, 78, 22);
		contentPane.add(comboBox5);

		comboBox6 = new JComboBox<>();
		comboBox6.setModel(new DefaultComboBoxModel<>(new String[] {"Very high", "High", "Medium", "Low", "Very low"}));
		comboBox6.setBounds(742, 291, 78, 22);
		contentPane.add(comboBox6);

		comboBox7 = new JComboBox<>();
		comboBox7.setModel(new DefaultComboBoxModel<>(new String[] {"Very high", "High", "Medium", "Low", "Very low"}));
		comboBox7.setBounds(742, 322, 78, 22);
		contentPane.add(comboBox7);

		btnDateTable = new JButton("Date Added");
		btnDateTable.setBounds(543, 409, 95, 25);
		btnDateTable.addMouseListener(this);
		contentPane.add(btnDateTable);

		lblNewLabel_2 = new JLabel("Importance");
		lblNewLabel_2.setBounds(742, 111, 61, 16);
		contentPane.add(lblNewLabel_2);

		lblNewLabel_3 = new JLabel("Interest");
		lblNewLabel_3.setBounds(649, 111, 39, 16);
		contentPane.add(lblNewLabel_3);

		separator = new JSeparator();
		separator.setBounds(0, 444, 835, 2);
		contentPane.add(separator);		
		
		countryComboBox.setModel(new DefaultComboBoxModel<>(getAllCountries()));
		
		setDefaultFeaturesInTextfields();
		customFeaturesDisable();
	}

	@Override
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
	}
}
