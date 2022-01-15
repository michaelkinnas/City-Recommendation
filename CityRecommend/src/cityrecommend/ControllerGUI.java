package cityrecommend;


import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JLabel;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.MouseInputListener;
import javax.swing.JCheckBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JSeparator;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.awt.Toolkit;
import java.awt.Color;
import java.awt.Font;
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


public class ControllerGUI extends JFrame implements MouseInputListener, ActionListener, WindowListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8395609496282147027L;	
	private static final String[] TERMS = new String[] {"bar","beach","restaurant","museum","hotel","transport","temple"};
	private static final String FILEPATH = "backup.json";

	private ArrayList<City> cities = new ArrayList<City>();

	private JPanel contentPane;
	private JCheckBox chckbxCustomRecommendation;
	private JPanel panelCustomSettings;	
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

	private DefaultListModel<String> modelSelectedCities = new DefaultListModel<>();
	private JList<String> listSelectedCities;

	private DefaultListModel<String> modelRecommendedCities = new DefaultListModel<>();
	private JList<String> listRecommended;	

	private JButton btnCovidInfo;
	private JTextField textFieldCityName;
	private JTextField textFieldCountryCode;
	private JLabel lblNewLabel_3;
	private JLabel lblNewLabel_4;
	private JButton btnAdd;
	private JButton btnRemove;
	private JButton btnClear;
	private JButton btnDateTable;
	private PerceptronTraveller pty;
	private PerceptronTraveller ptm;
	private PerceptronTraveller pte;
	private PerceptronTraveller ptc;

	private ArrayList<City> recommendedYoung = new ArrayList<>();
	private ArrayList<City> recommendedMiddle  = new ArrayList<>();
	private ArrayList<City> recommendedElder = new ArrayList<>();
	private ArrayList<City> recommendedCustom = new ArrayList<>();

	private int createCitySemaphore = 0; //for creating cities threads
	private int retrieveDataSemaphore = 0; //for retrieving rest of city data from APIs	

	private double[] customTermsBias = new double[7];
	private String[] customTerms = new String[7];

	private CovidFrame cvframe;
	private File saveFile;
	private JComboBox<String> comboBoxSorting;

	private DateAdded dateFrame;

	private static final Logger LOGGER = Logger.getLogger(ControllerGUI.class.getName());
	/**
	 * Create the frame.
	 * @throws UnsupportedLookAndFeelException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws ClassNotFoundException 
	 */
	public ControllerGUI() throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {	
		Handler consoleHandler = null;
		Handler fileHandler  = null;

		try {
			//Creating consoleHandler and fileHandler
			consoleHandler = new ConsoleHandler();
			fileHandler  = new FileHandler("./log.txt");

			//Assigning handlers to LOGGER object
			LOGGER.addHandler(consoleHandler);
			LOGGER.addHandler(fileHandler);

			//Setting levels to handlers and LOGGER
			consoleHandler.setLevel(Level.ALL);
			fileHandler.setLevel(Level.ALL);
			LOGGER.setLevel(Level.ALL);

			LOGGER.config("Configuration done.");

			//Console handler removed
			LOGGER.removeHandler(consoleHandler);

		} catch (IOException exception) {
			LOGGER.log(Level.SEVERE, "Error occur in FileHandler.", exception);
		}



		drawGui();
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
		modelSelectedCities.removeAllElements();
		for (City city: cities) {
			modelSelectedCities.addElement(city.getCityName() + " " +  city.getCountryCode());
		}		
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
			} else {
				for (City city: cities) {
					if (chckbxCustomRecommendation.isSelected() && !(city.getDataSource() == 2)) {
						LOGGER.log(Level.FINE, "Retrieving data for cities.");
						retrieveDataSemaphoreUp();
						retrieveCityData(city, this.customTerms, 2);					
					} else if (!chckbxCustomRecommendation.isSelected() && !(city.getDataSource() == 1)) {
						LOGGER.log(Level.FINE, "Retrieving data for cities with custom data.");
						retrieveDataSemaphoreUp();
						retrieveCityData(city, TERMS, 1);
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
			int index = listSelectedCities.getSelectedIndex();
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
			addCity(textFieldCityName.getText(), textFieldCountryCode.getText());
			textFieldCityName.setText("");
			textFieldCountryCode.setText("");
			textFieldCityName.requestFocus();
		} 

		if (e.getSource() == btnClear) {
			LOGGER.log(Level.FINE, "Clicked Clear button.");
			modelSelectedCities.removeAllElements();
			modelRecommendedCities.removeAllElements();

			recommendedYoung = new ArrayList<>();
			recommendedMiddle = new ArrayList<>();
			recommendedElder = new ArrayList<>();
			recommendedCustom = new ArrayList<>();
			cities = new ArrayList<>();

			textField1.setText("");
			textField2.setText("");
			textField3.setText("");
			textField4.setText("");
			textField5.setText("");
			textField6.setText("");
			textField7.setText("");

			comboBox1.setSelectedIndex(0);
			comboBox2.setSelectedIndex(0);
			comboBox3.setSelectedIndex(0);
			comboBox4.setSelectedIndex(0);
			comboBox5.setSelectedIndex(0);
			comboBox6.setSelectedIndex(0);
			comboBox7.setSelectedIndex(0);

			textFieldCityName.setText("");
			textFieldCountryCode.setText("");
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
			int index = listRecommended.getSelectedIndex();

			if (index == -1) {
				JOptionPane.showMessageDialog(null, "You must select a city first.", "No city selected", JOptionPane.WARNING_MESSAGE);
				LOGGER.log(Level.WARNING, "Clicked COVID info button without selecting cities.");
			} else {
				cvframe.setTextAreaText("");
				City city = cities.get(index);				
				cvframe.setTitle("COVID19 information for " + city.getCityName() + ", " + city.getCountryCode() );

				setCovidData(city);							

				cvframe.setVisible(true);
			}			
		}
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
				city.retrieveCovidData();
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
	 * Adds the appropriate recommended ArrayList results to the model of the recommended JList in the GUI
	 * according to the perceptron selected. 
	 */
	private void displayResults() {	
		modelRecommendedCities.removeAllElements();
		if (chckbxCustomRecommendation.isSelected()) {		
			for (int i = 0; i < recommendedCustom.size(); i++) {				
				modelRecommendedCities.addElement(recommendedCustom.get(i).getCityName() + " " +  recommendedCustom.get(i).getCountryCode());
			}
		} else {
			if (comboBoxAgeRange.getSelectedIndex() == 0) {				
				for (int i = 0; i < recommendedYoung.size(); i++) {
					modelRecommendedCities.addElement(recommendedYoung.get(i).getCityName() + " " +  recommendedYoung.get(i).getCountryCode());
				}
			} else if (comboBoxAgeRange.getSelectedIndex() == 1) {				
				for (int i = 0; i < recommendedMiddle.size(); i++) {				
					modelRecommendedCities.addElement(recommendedMiddle.get(i).getCityName() + " " +  recommendedMiddle.get(i).getCountryCode());
				}
			} else {			
				for (int i = 0; i < recommendedElder.size(); i++) {				
					modelRecommendedCities.addElement(recommendedElder.get(i).getCityName() + " " +  recommendedElder.get(i).getCountryCode());
				}
			}
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
	
	/**
	 * ActionListener interface method implementation.
	 * @param ActionEvent
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (chckbxCustomRecommendation.isSelected()) {
			LOGGER.log(Level.FINE, "Checked custom recommendations checkbox.");
			panelCustomSettings.setVisible(true);
		} else {
			LOGGER.log(Level.FINE, "Unchecked custom recommendations checkbox.");
			panelCustomSettings.setVisible(false);
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
	
	/**
	 * Reads the custom data given by a user from the GUI.
	 */
	private void getCustomData() {
		int[] selections = new int[7];

		this.customTerms[0] = textField1.getSelectedText();
		this.customTerms[1] = textField2.getSelectedText();
		this.customTerms[2] = textField3.getSelectedText();
		this.customTerms[3] = textField4.getSelectedText();
		this.customTerms[4] = textField5.getSelectedText();
		this.customTerms[5] = textField6.getSelectedText();
		this.customTerms[6] = textField7.getSelectedText();

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
					temp = new City(cityName, countryCode, TERMS, date.getTime());
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

	@SuppressWarnings("unused")
	private void DEBUG_printCityScores() {
		//DEBUGING Print socres of recommendations

		System.out.println("Custom");
		for (int i=0; i < this.recommendedCustom.size(); i++) {
			System.out.println(recommendedCustom.get(i).getCityName() + " " + recommendedCustom.get(i).getScore());
		}
		System.out.println("Young");
		for (int i=0; i < this.recommendedYoung.size(); i++) {
			System.out.println(recommendedYoung.get(i).getCityName() + " " + recommendedYoung.get(i).getScore());
		}
		System.out.println("Middle");
		for (int i=0; i < this.recommendedMiddle.size(); i++) {
			System.out.println(recommendedMiddle.get(i).getCityName() + " " + recommendedMiddle.get(i).getScore());
		}
		System.out.println("Elder");
		for (int i=0; i < this.recommendedElder.size(); i++) {
			System.out.println(recommendedElder.get(i).getCityName() + " " + recommendedElder.get(i).getScore());
		}
		System.out.println();
	}

	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub

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
		for (int i = 0; i < city.getCovidData().getData().getAreaAccessRestriction().getEntry().getBannedArea().size(); i++) {			
			infoArrayList.add(city.getCovidData().getData().getAreaAccessRestriction().getEntry().getBannedArea().get(i).getName());
			infoArrayList.add(" ");
			infoArrayList.add(city.getCovidData().getData().getAreaAccessRestriction().getEntry().getBannedArea().get(i).getIataCode());
			//infoarray.add(" ";
			//infoarray.add(city.getCovidData().getData().getAreaAccessRestriction().getEntry().getBannedArea().get(i).getAreaType();
			infoArrayList.add(", ");
		}
		infoArrayList.add("\n");
		infoArrayList.add("Border ban: ");
		//infoarray.add("Border ban: " + city.getCovidData().getData().getAreaAccessRestriction().getEntry().getBorderBan()+"\n");
		for (int i = 0; i < city.getCovidData().getData().getAreaAccessRestriction().getEntry().getBorderBan().size(); i++) {			
			infoArrayList.add(city.getCovidData().getData().getAreaAccessRestriction().getEntry().getBorderBan().get(i).getBorderType());
			infoArrayList.add(" ");
			infoArrayList.add(city.getCovidData().getData().getAreaAccessRestriction().getEntry().getBorderBan().get(i).getStatus());
			//infoarray.add(" ";
			//infoarray.add(city.getCovidData().getData().getAreaAccessRestriction().getEntry().getBannedArea().get(i).getAreaType();
			infoArrayList.add(", ");
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
		for (int i = 0; i < city.getCovidData().getData().getAreaAccessRestriction().getQuarantineModality().getQuarantineOnArrivalAreas().size(); i++) {			
			infoArrayList.add(city.getCovidData().getData().getAreaAccessRestriction().getQuarantineModality().getQuarantineOnArrivalAreas().get(i).getIataCode());
			//infoarray.add(" ";
			//infoarray.add(city.getCovidData().getData().getAreaAccessRestriction().getQuarantineModality().getQuarantineOnArrivalAreas().get(i).getAreaType();
			//infoarray.add(" ";
			//infoarray.add(city.getCovidData().getData().getAreaAccessRestriction().getQuarantineModality().getQuarantineOnArrivalAreas().get(i).getAdditionalProperties();
			infoArrayList.add(", ");
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
		infoArrayList.add("\n");
		
		//infoarray.add("Area vaccinated: " + city.getCovidData().getData().getAreaVaccinated() +"\n\n";
		infoArrayList.add("AREA VACCINATED:\n");
		for (int i = 0; i <city.getCovidData().getData().getAreaVaccinated().size(); i++) {			
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
	 * Parameters for the GUI creation process.
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws UnsupportedLookAndFeelException
	 */
	private void drawGui() throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
		setResizable(false);
		setTitle("City Recommend");
		setIconImage(Toolkit.getDefaultToolkit().getImage(ControllerGUI.class.getResource("/graphics/MainIcon.png")));
		UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		addWindowListener(this);		

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 775, 523);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		listSelectedCities = new JList<>( modelSelectedCities );
		listSelectedCities.setFont(new Font("Tahoma", Font.PLAIN, 12));
		listSelectedCities.setToolTipText("List of seected places to get recommendations from. Select an item and click the remove button to remove it from the list.");
		listSelectedCities.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		listSelectedCities.setBounds(10, 127, 218, 249);
		contentPane.add(listSelectedCities);

		JLabel lblNewLabel = new JLabel("Selected places");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblNewLabel.setBounds(10, 12, 93, 15);
		contentPane.add(lblNewLabel);

		btnRecommend = new JButton("Recommend");
		btnRecommend.setToolTipText("Get recommendations!");
		btnRecommend.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnRecommend.setBounds(315, 180, 125, 23);
		btnRecommend.addMouseListener(this);		
		contentPane.add(btnRecommend);

		chckbxCustomRecommendation = new JCheckBox("Customize criteria");
		chckbxCustomRecommendation.setToolTipText("Enable customized criteria for you!");
		chckbxCustomRecommendation.setFont(new Font("Tahoma", Font.PLAIN, 12));
		chckbxCustomRecommendation.setBounds(315, 210, 121, 23);
		chckbxCustomRecommendation.addActionListener(this);
		contentPane.add(chckbxCustomRecommendation);

		panelCustomSettings = new JPanel();
		panelCustomSettings.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		panelCustomSettings.setBounds(10, 387, 739, 69);
		contentPane.add(panelCustomSettings);
		panelCustomSettings.setLayout(null);

		JLabel lblNewLabel_1 = new JLabel("Interests:");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblNewLabel_1.setBounds(8, 12, 53, 15);
		panelCustomSettings.add(lblNewLabel_1);

		textField1 = new JTextField();
		textField1.setToolTipText("Type any criteria ex. \"Restaurant\" or \"Hotel\"");
		textField1.setFont(new Font("Tahoma", Font.PLAIN, 12));
		textField1.setBounds(71, 9, 85, 20);
		panelCustomSettings.add(textField1);
		textField1.setColumns(10);

		JLabel lblNewLabel_1_1 = new JLabel("Opinion:");
		lblNewLabel_1_1.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblNewLabel_1_1.setBounds(8, 43, 45, 15);
		panelCustomSettings.add(lblNewLabel_1_1);		

		comboBox1 = new JComboBox<String>();
		comboBox1.setToolTipText("Select the order of importance for you, for the custom interest.");
		comboBox1.setModel(new DefaultComboBoxModel<String>(new String[] {"Love", "Like", "Indifferent", "Dislike", "Hate"}));
		comboBox1.setFont(new Font("Tahoma", Font.PLAIN, 12));
		comboBox1.setBounds(71, 40, 85, 21);
		panelCustomSettings.add(comboBox1);

		textField2 = new JTextField();
		textField2.setBounds(167, 9, 85, 20);
		panelCustomSettings.add(textField2);
		textField2.setToolTipText("Type any criteria ex. \"Restaurant\" or \"Hotel\"");
		textField2.setFont(new Font("Tahoma", Font.PLAIN, 12));
		textField2.setColumns(10);

		textField3 = new JTextField();
		textField3.setBounds(263, 9, 85, 20);
		panelCustomSettings.add(textField3);
		textField3.setToolTipText("Type any criteria ex. \"Restaurant\" or \"Hotel\"");
		textField3.setFont(new Font("Tahoma", Font.PLAIN, 12));
		textField3.setColumns(10);

		textField4 = new JTextField();
		textField4.setBounds(359, 9, 85, 20);
		panelCustomSettings.add(textField4);
		textField4.setToolTipText("Type any criteria ex. \"Restaurant\" or \"Hotel\"");
		textField4.setFont(new Font("Tahoma", Font.PLAIN, 12));
		textField4.setColumns(10);

		textField5 = new JTextField();
		textField5.setBounds(455, 9, 85, 20);
		panelCustomSettings.add(textField5);
		textField5.setToolTipText("Type any criteria ex. \"Restaurant\" or \"Hotel\"");
		textField5.setFont(new Font("Tahoma", Font.PLAIN, 12));
		textField5.setColumns(10);

		textField6 = new JTextField();
		textField6.setBounds(551, 9, 85, 20);
		panelCustomSettings.add(textField6);
		textField6.setToolTipText("Type any criteria ex. \"Restaurant\" or \"Hotel\"");
		textField6.setFont(new Font("Tahoma", Font.PLAIN, 12));
		textField6.setColumns(10);

		textField7 = new JTextField();
		textField7.setBounds(647, 9, 85, 20);
		panelCustomSettings.add(textField7);
		textField7.setToolTipText("Type any criteria ex. \"Restaurant\" or \"Hotel\"");
		textField7.setFont(new Font("Tahoma", Font.PLAIN, 12));
		textField7.setColumns(10);

		comboBox7 = new JComboBox<String>();
		comboBox7.setBounds(647, 40, 85, 21);
		panelCustomSettings.add(comboBox7);
		comboBox7.setToolTipText("Select the order of importance for you, for the custom interest.");
		comboBox7.setModel(new DefaultComboBoxModel<String>(new String[] {"Love", "Like", "Indifferent", "Dislike", "Hate"}));
		comboBox7.setFont(new Font("Tahoma", Font.PLAIN, 12));

		comboBox6 = new JComboBox<String>();
		comboBox6.setBounds(551, 40, 85, 21);
		panelCustomSettings.add(comboBox6);
		comboBox6.setToolTipText("Select the order of importance for you, for the custom interest.");
		comboBox6.setModel(new DefaultComboBoxModel<String>(new String[] {"Love", "Like", "Indifferent", "Dislike", "Hate"}));
		comboBox6.setFont(new Font("Tahoma", Font.PLAIN, 12));

		comboBox5 = new JComboBox<String>();
		comboBox5.setBounds(455, 40, 85, 21);
		panelCustomSettings.add(comboBox5);
		comboBox5.setToolTipText("Select the order of importance for you, for the custom interest.");
		comboBox5.setModel(new DefaultComboBoxModel<String>(new String[] {"Love", "Like", "Indifferent", "Dislike", "Hate"}));
		comboBox5.setFont(new Font("Tahoma", Font.PLAIN, 12));

		comboBox4 = new JComboBox<String>();
		comboBox4.setBounds(359, 40, 85, 21);
		panelCustomSettings.add(comboBox4);
		comboBox4.setToolTipText("Select the order of importance for you, for the custom interest.");
		comboBox4.setModel(new DefaultComboBoxModel<String>(new String[] {"Love", "Like", "Indifferent", "Dislike", "Hate"}));
		comboBox4.setFont(new Font("Tahoma", Font.PLAIN, 12));

		comboBox3 = new JComboBox<String>();
		comboBox3.setBounds(263, 40, 85, 21);
		panelCustomSettings.add(comboBox3);
		comboBox3.setToolTipText("Select the order of importance for you, for the custom interest.");
		comboBox3.setModel(new DefaultComboBoxModel<String>(new String[] {"Love", "Like", "Indifferent", "Dislike", "Hate"}));
		comboBox3.setFont(new Font("Tahoma", Font.PLAIN, 12));

		comboBox2 = new JComboBox<String>();
		comboBox2.setBounds(167, 40, 85, 21);
		panelCustomSettings.add(comboBox2);
		comboBox2.setToolTipText("Select the order of importance for you, for the custom interest.");
		comboBox2.setModel(new DefaultComboBoxModel<String>(new String[] {"Love", "Like", "Indifferent", "Dislike", "Hate"}));
		comboBox2.setFont(new Font("Tahoma", Font.PLAIN, 12));

		JLabel lblRecommendations = new JLabel("Recommendations");
		lblRecommendations.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblRecommendations.setBounds(530, 12, 113, 15);
		contentPane.add(lblRecommendations);

		listRecommended = new JList<>(modelRecommendedCities);
		listRecommended.setFont(new Font("Tahoma", Font.PLAIN, 12));
		listRecommended.setToolTipText("List of recommended cities for you. Select a place from the list and click \"COVID Info\" button to get COVID information.");
		listRecommended.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		listRecommended.setBounds(530, 65, 218, 277);
		contentPane.add(listRecommended);

		btnCovidInfo = new JButton("COVID info");
		btnCovidInfo.setToolTipText("Get COVID information for selected place");
		btnCovidInfo.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnCovidInfo.setBounds(531, 353, 218, 23);
		btnCovidInfo.addMouseListener(this);
		contentPane.add(btnCovidInfo);

		lblNewLabel_2 = new JLabel("Choose your age range:");
		lblNewLabel_2.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblNewLabel_2.setBounds(273, 152, 131, 15);
		contentPane.add(lblNewLabel_2);

		comboBoxAgeRange = new JComboBox<String>();
		comboBoxAgeRange.setModel(new DefaultComboBoxModel<String>(new String[] {"0 - 29", "30 - 59", "60 - 100"}));
		comboBoxAgeRange.setFont(new Font("Tahoma", Font.PLAIN, 12));
		comboBoxAgeRange.setBounds(412, 149, 74, 21);
		contentPane.add(comboBoxAgeRange);

		textFieldCityName = new JTextField();
		textFieldCityName.setToolTipText("Type a city name, ex. Athens");
		textFieldCityName.setFont(new Font("Tahoma", Font.PLAIN, 12));
		textFieldCityName.setColumns(10);
		textFieldCityName.setBounds(78, 34, 150, 20);
		contentPane.add(textFieldCityName);

		textFieldCountryCode = new JTextField();
		textFieldCountryCode.setToolTipText("Country code in ISO 3166 format. (ex. for Greece type GR)");
		textFieldCountryCode.setFont(new Font("Tahoma", Font.PLAIN, 12));
		textFieldCountryCode.setColumns(10);
		textFieldCountryCode.setBounds(156, 63, 72, 20);
		contentPane.add(textFieldCountryCode);

		lblNewLabel_3 = new JLabel("City name:");
		lblNewLabel_3.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblNewLabel_3.setBounds(10, 37, 58, 15);
		contentPane.add(lblNewLabel_3);

		lblNewLabel_4 = new JLabel("Country Code (optional):");
		lblNewLabel_4.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblNewLabel_4.setBounds(10, 66, 136, 15);
		contentPane.add(lblNewLabel_4);

		btnAdd = new JButton("Add");
		btnAdd.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnAdd.addMouseListener(this);
		btnAdd.setBounds(10, 94, 65, 23);
		contentPane.add(btnAdd);

		btnRemove = new JButton("Remove");
		btnRemove.setToolTipText("Remove selected city from list");
		btnRemove.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnRemove.setBounds(82, 94, 77, 23);
		btnRemove.addMouseListener(this);
		contentPane.add(btnRemove);

		btnClear = new JButton("Clear");
		btnClear.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnClear.setBounds(166, 94, 62, 23);
		btnClear.addMouseListener(this);
		contentPane.add(btnClear);

		lblStatus = new JLabel("Ready");
		lblStatus.setForeground(Color.GRAY);
		lblStatus.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblStatus.setBounds(10, 466, 208, 15);
		contentPane.add(lblStatus);

		separator_1 = new JSeparator();
		separator_1.setBounds(0, 461, 763, 2);
		contentPane.add(separator_1);

		comboBoxSorting = new JComboBox<String>();
		comboBoxSorting.setFont(new Font("Tahoma", Font.PLAIN, 12));
		comboBoxSorting.setModel(new DefaultComboBoxModel<String>(new String[] {"Sort by score | Ascending", "Sort by score | Descending", "Sort by distance | Ascending", "Sort by distance reversed | Descending", "Sort by time added | Ascending", "Sort by time added reversed | Descending"}));
		comboBoxSorting.setBounds(530, 33, 218, 21);
		comboBoxSorting.addActionListener(this);
		contentPane.add(comboBoxSorting);

		btnDateTable = new JButton("Date Added");
		btnDateTable.setBounds(238, 354, 89, 23);
		btnDateTable.addMouseListener(this);
		contentPane.add(btnDateTable);
		panelCustomSettings.setVisible(false);
	}
}