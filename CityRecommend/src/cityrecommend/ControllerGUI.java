package cityrecommend;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JLabel;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.MouseInputListener;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import comparators.ScoreCompare;
import exceptions.AmadeusErrorException;
import exceptions.FoursquareNoCityException;
import exceptions.OpenWeatherCityNotFoundException;
import javax.swing.JCheckBox;

import java.awt.Font;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JSeparator;
import java.awt.Toolkit;

public class ControllerGUI extends JFrame implements MouseInputListener, ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8395609496282147027L;

	private static final String OPEN_WEATHER_APPID_22046 = "32fc4065e28603f29c061d7064f10147";
	private static final String FOURSQUARE_APPID_22046 = "fsq3V4uGPDvrRFXcv6I2sgiuT85a2KdNFva9nW0yBmfO5c0=";
	private static final String[] TERMS = new String[] {"bar","beach","restaurant","museum","hotel","transport","temple"};

	private ArrayList<City> cities = new ArrayList<City>();

	private JPanel contentPane;
	private JCheckBox chckbxCustomRecommendation;
	private JPanel panelCustomSettings;	

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
	private JButton btnDelete;
	private JButton btnClear;

	private PerceptronTraveller pty = new PerceptronYoungTraveller();
	private PerceptronTraveller ptm = new PerceptronMiddleTraveller();
	private PerceptronTraveller pte = new PerceptronElderTraveller();
	private PerceptronTraveller ptc;

	private ArrayList<City> recommendedYoung = new ArrayList<>();
	private ArrayList<City> recommendedMiddle  = new ArrayList<>();
	private ArrayList<City> recommendedElder = new ArrayList<>();
	private ArrayList<City> recommendedCustom = new ArrayList<>();

	private int createCitySemaphore = 0; //for creating cities threads
	private int retrieveDataSemaphore = 0; //for retrieving rest of city data from APIs	

	private double[] customTermsBias = new double[10];
	private String[] customTerms = new String[7];

	private int cityDataStatus = 0; //0:no data retrieved 1:default, 2:custom


	/**
	 * Create the frame.
	 * @throws UnsupportedLookAndFeelException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws ClassNotFoundException 
	 */
	public ControllerGUI() throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
		setResizable(false);
		setTitle("City Recommend");
		setIconImage(Toolkit.getDefaultToolkit().getImage(ControllerGUI.class.getResource("/graphics/buildings.png")));
		UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 787, 516);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		listSelectedCities = new JList<>( modelSelectedCities );
		listSelectedCities.setFont(new Font("Tahoma", Font.PLAIN, 12));
		listSelectedCities.setToolTipText("List of seected places to get recommendations from. Select an item and click the remove button to remove it from the list.");
		listSelectedCities.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		listSelectedCities.setBounds(10, 128, 245, 249);
		contentPane.add(listSelectedCities);

		JLabel lblNewLabel = new JLabel("Selected places");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblNewLabel.setBounds(10, 103, 85, 15);
		contentPane.add(lblNewLabel);

		btnRecommend = new JButton("Recommend me!");
		btnRecommend.setToolTipText("Get recommendations!");
		btnRecommend.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnRecommend.setBounds(313, 181, 125, 23);
		btnRecommend.addMouseListener(this);		
		contentPane.add(btnRecommend);

		chckbxCustomRecommendation = new JCheckBox("Customize criteria");
		chckbxCustomRecommendation.setToolTipText("Enable customized criteria for you!");
		chckbxCustomRecommendation.setFont(new Font("Tahoma", Font.PLAIN, 12));
		chckbxCustomRecommendation.setBounds(313, 211, 179, 23);
		chckbxCustomRecommendation.addActionListener(this);
		contentPane.add(chckbxCustomRecommendation);

		panelCustomSettings = new JPanel();
		panelCustomSettings.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		panelCustomSettings.setBounds(10, 400, 753, 69);
		contentPane.add(panelCustomSettings);
		panelCustomSettings.setLayout(null);

		JLabel lblNewLabel_1 = new JLabel("Interests:");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblNewLabel_1.setBounds(22, 12, 53, 15);
		panelCustomSettings.add(lblNewLabel_1);

		textField1 = new JTextField();
		textField1.setToolTipText("Type any criteria ex. \"Restaurant\" or \"Hotel\"");
		textField1.setFont(new Font("Tahoma", Font.PLAIN, 12));
		textField1.setBounds(81, 9, 86, 20);
		panelCustomSettings.add(textField1);
		textField1.setColumns(10);

		JLabel lblNewLabel_1_1 = new JLabel("Importance:");
		lblNewLabel_1_1.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblNewLabel_1_1.setBounds(8, 43, 67, 15);
		panelCustomSettings.add(lblNewLabel_1_1);		

		textField2 = new JTextField();
		textField2.setToolTipText("Type any criteria ex. \"Restaurant\" or \"Hotel\"");
		textField2.setFont(new Font("Tahoma", Font.PLAIN, 12));
		textField2.setColumns(10);
		textField2.setBounds(177, 9, 86, 20);
		panelCustomSettings.add(textField2);

		textField3 = new JTextField();
		textField3.setToolTipText("Type any criteria ex. \"Restaurant\" or \"Hotel\"");
		textField3.setFont(new Font("Tahoma", Font.PLAIN, 12));
		textField3.setColumns(10);
		textField3.setBounds(273, 9, 86, 20);
		panelCustomSettings.add(textField3);

		textField4 = new JTextField();
		textField4.setToolTipText("Type any criteria ex. \"Restaurant\" or \"Hotel\"");
		textField4.setFont(new Font("Tahoma", Font.PLAIN, 12));
		textField4.setColumns(10);
		textField4.setBounds(369, 9, 86, 20);
		panelCustomSettings.add(textField4);

		textField5 = new JTextField();
		textField5.setToolTipText("Type any criteria ex. \"Restaurant\" or \"Hotel\"");
		textField5.setFont(new Font("Tahoma", Font.PLAIN, 12));
		textField5.setColumns(10);
		textField5.setBounds(465, 9, 86, 20);
		panelCustomSettings.add(textField5);

		textField6 = new JTextField();
		textField6.setToolTipText("Type any criteria ex. \"Restaurant\" or \"Hotel\"");
		textField6.setFont(new Font("Tahoma", Font.PLAIN, 12));
		textField6.setColumns(10);
		textField6.setBounds(561, 9, 86, 20);
		panelCustomSettings.add(textField6);

		textField7 = new JTextField();
		textField7.setToolTipText("Type any criteria ex. \"Restaurant\" or \"Hotel\"");
		textField7.setFont(new Font("Tahoma", Font.PLAIN, 12));
		textField7.setColumns(10);
		textField7.setBounds(657, 9, 86, 20);
		panelCustomSettings.add(textField7);

		comboBox1 = new JComboBox<String>();
		comboBox1.setToolTipText("Select the order of importance for you, for the custom interest.");
		comboBox1.setModel(new DefaultComboBoxModel<String>(new String[] {"Love", "Like", "Indifferent", "Dislike", "Hate"}));
		comboBox1.setFont(new Font("Tahoma", Font.PLAIN, 12));
		comboBox1.setBounds(81, 40, 86, 21);
		panelCustomSettings.add(comboBox1);

		comboBox2 = new JComboBox<String>();
		comboBox2.setToolTipText("Select the order of importance for you, for the custom interest.");
		comboBox2.setModel(new DefaultComboBoxModel<String>(new String[] {"Love", "Like", "Indifferent", "Dislike", "Hate"}));
		comboBox2.setFont(new Font("Tahoma", Font.PLAIN, 12));
		comboBox2.setBounds(177, 40, 86, 21);
		panelCustomSettings.add(comboBox2);

		comboBox3 = new JComboBox<String>();
		comboBox3.setToolTipText("Select the order of importance for you, for the custom interest.");
		comboBox3.setModel(new DefaultComboBoxModel<String>(new String[] {"Love", "Like", "Indifferent", "Dislike", "Hate"}));
		comboBox3.setFont(new Font("Tahoma", Font.PLAIN, 12));
		comboBox3.setBounds(273, 40, 86, 21);
		panelCustomSettings.add(comboBox3);

		comboBox4 = new JComboBox<String>();
		comboBox4.setToolTipText("Select the order of importance for you, for the custom interest.");
		comboBox4.setModel(new DefaultComboBoxModel<String>(new String[] {"Love", "Like", "Indifferent", "Dislike", "Hate"}));
		comboBox4.setFont(new Font("Tahoma", Font.PLAIN, 12));
		comboBox4.setBounds(369, 40, 86, 21);
		panelCustomSettings.add(comboBox4);

		comboBox5 = new JComboBox<String>();
		comboBox5.setToolTipText("Select the order of importance for you, for the custom interest.");
		comboBox5.setModel(new DefaultComboBoxModel<String>(new String[] {"Love", "Like", "Indifferent", "Dislike", "Hate"}));
		comboBox5.setFont(new Font("Tahoma", Font.PLAIN, 12));
		comboBox5.setBounds(465, 40, 86, 21);
		panelCustomSettings.add(comboBox5);

		comboBox6 = new JComboBox<String>();
		comboBox6.setToolTipText("Select the order of importance for you, for the custom interest.");
		comboBox6.setModel(new DefaultComboBoxModel<String>(new String[] {"Love", "Like", "Indifferent", "Dislike", "Hate"}));
		comboBox6.setFont(new Font("Tahoma", Font.PLAIN, 12));
		comboBox6.setBounds(561, 40, 86, 21);
		panelCustomSettings.add(comboBox6);

		comboBox7 = new JComboBox<String>();
		comboBox7.setToolTipText("Select the order of importance for you, for the custom interest.");
		comboBox7.setModel(new DefaultComboBoxModel<String>(new String[] {"Love", "Like", "Indifferent", "Dislike", "Hate"}));
		comboBox7.setFont(new Font("Tahoma", Font.PLAIN, 12));
		comboBox7.setBounds(657, 40, 86, 21);
		panelCustomSettings.add(comboBox7);

		JLabel lblRecommendations = new JLabel("Recommendations");
		lblRecommendations.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblRecommendations.setBounds(498, 12, 100, 15);
		contentPane.add(lblRecommendations);

		listRecommended = new JList<>(modelRecommendedCities);
		listRecommended.setFont(new Font("Tahoma", Font.PLAIN, 12));
		listRecommended.setToolTipText("List of recommended cities for you. Select a place from the list and click \"COVID Info\" button to get COVID information.");
		listRecommended.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		listRecommended.setBounds(498, 37, 265, 305);
		contentPane.add(listRecommended);

		JSeparator separator = new JSeparator();
		separator.setBounds(12, 387, 751, 2);
		contentPane.add(separator);

		btnCovidInfo = new JButton("COVID info");
		btnCovidInfo.setToolTipText("Get COVID information for selected place");
		btnCovidInfo.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnCovidInfo.setBounds(498, 352, 265, 23);
		contentPane.add(btnCovidInfo);

		lblNewLabel_2 = new JLabel("Choose your age range:");
		lblNewLabel_2.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblNewLabel_2.setBounds(268, 152, 126, 15);
		contentPane.add(lblNewLabel_2);

		comboBoxAgeRange = new JComboBox<String>();
		comboBoxAgeRange.setModel(new DefaultComboBoxModel<String>(new String[] {"0 - 29", "30 - 59", "60 - 100"}));
		comboBoxAgeRange.setFont(new Font("Tahoma", Font.PLAIN, 12));
		comboBoxAgeRange.setBounds(404, 149, 74, 21);
		contentPane.add(comboBoxAgeRange);

		textFieldCityName = new JTextField();
		textFieldCityName.setFont(new Font("Tahoma", Font.PLAIN, 12));
		textFieldCityName.setColumns(10);
		textFieldCityName.setBounds(105, 11, 150, 20);
		contentPane.add(textFieldCityName);

		textFieldCountryCode = new JTextField();
		textFieldCountryCode.setFont(new Font("Tahoma", Font.PLAIN, 12));
		textFieldCountryCode.setColumns(10);
		textFieldCountryCode.setBounds(105, 39, 150, 20);
		contentPane.add(textFieldCountryCode);

		lblNewLabel_3 = new JLabel("City name:");
		lblNewLabel_3.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblNewLabel_3.setBounds(10, 11, 58, 15);
		contentPane.add(lblNewLabel_3);

		lblNewLabel_4 = new JLabel("Country Code:");
		lblNewLabel_4.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblNewLabel_4.setBounds(10, 42, 79, 15);
		contentPane.add(lblNewLabel_4);

		btnAdd = new JButton("Add");
		btnAdd.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnAdd.addMouseListener(this);
		btnAdd.setBounds(10, 70, 85, 23);
		contentPane.add(btnAdd);

		btnDelete = new JButton("Delete");
		btnDelete.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnDelete.setBounds(105, 70, 65, 23);
		contentPane.add(btnDelete);

		btnClear = new JButton("Clear");
		btnClear.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnClear.setBounds(181, 70, 74, 23);
		btnClear.addMouseListener(this);
		contentPane.add(btnClear);

		lblStatus = new JLabel("Ready");
		lblStatus.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblStatus.setBounds(265, 362, 208, 15);
		contentPane.add(lblStatus);
		panelCustomSettings.setVisible(false);
	}

	@Override
	public void mouseClicked(MouseEvent e) {		
		if (e.getSource() == btnRecommend) {
			if (cities.isEmpty()) {
				//TODO show message about adding cities
			} 

			if (chckbxCustomRecommendation.isSelected() && (this.cityDataStatus == 0 || this.cityDataStatus == 1)) {
				this.cityDataStatus = 2;
				getCustomData();
				ptc = new PerceptronCustomTraveller(this.customTermsBias);
				for (City c: cities) {
					retrieveDataSemaphoreUp();
					retrieveCityData(c, this.customTerms);
				}				
			} else if (chckbxCustomRecommendation.isSelected() && this.cityDataStatus == 2) {
				getCustomData();
				ptc = new PerceptronCustomTraveller(this.customTermsBias);
				makeRecommendations();
				sortRecommendations();
				displayResults();
			} else if (!chckbxCustomRecommendation.isSelected() && (this.cityDataStatus == 0 || this.cityDataStatus == 2)) {				
				this.cityDataStatus = 1;
				for (City c: cities) {					
					retrieveDataSemaphoreUp();
					retrieveCityData(c, ControllerGUI.TERMS);
				}
			} else if (!chckbxCustomRecommendation.isSelected() && this.cityDataStatus == 1) {				
				displayResults();
			}
			
			//DEBUGING Print socres of recommendations
			/*
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
			}*/
		}
		
		


		if (e.getSource() == btnAdd) {
			createCitySemaphoreUp();
			addCity(textFieldCityName.getText(), textFieldCountryCode.getText());
			textFieldCityName.setText("");
			textFieldCountryCode.setText("");

		} 

		if (e.getSource() == btnClear) {
			modelSelectedCities.removeAllElements();
			textFieldCityName.setText("");
			textFieldCountryCode.setText("");

		}
		textFieldCityName.requestFocus();

	}

	private void retrieveCityData(City c, String[] terms) {		
		SwingWorker<Void, Void> worker = new SwingWorker<>() {
			@Override
			protected Void doInBackground() throws Exception {
				c.setTerms(terms);
				c.retrieveFeatureScore();
				c.normalise();
				//c.retrieveCovidData();				
				return null;
			}
			protected void done() {				
				retrieveDataSemaphoreDown();
			}			
		};
		worker.execute();


	}

	private synchronized void retrieveDataSemaphoreUp() {
		this.retrieveDataSemaphore++;
		this.btnRecommend.setEnabled(false);
		this.lblStatus.setText("Please wait, working...");
	}

	private synchronized void retrieveDataSemaphoreDown() {
		this.retrieveDataSemaphore--; //if retrieve data threads have finished run the recommendation perceptrons		

		if (retrieveDataSemaphore == 0) {
			makeRecommendations();
			sortRecommendations();
			displayResults();
		}
		this.btnRecommend.setEnabled(true);
		this.lblStatus.setText("Ready");
	}
	
	//Sort recommendations by score
	private void sortRecommendations() {
		ScoreCompare scores = new ScoreCompare();
		Collections.sort(recommendedYoung, scores);
		Collections.sort(recommendedMiddle, scores);	
		Collections.sort(recommendedElder, scores);	
		Collections.sort(recommendedCustom, scores);	
	}

	private void displayResults() {
		modelRecommendedCities.removeAllElements();
		if (chckbxCustomRecommendation.isSelected()) {			
			for ( int i = 0; i < recommendedCustom.size(); i++ ) {				
				modelRecommendedCities.addElement(recommendedCustom.get(i).getCityName() + " " +  recommendedCustom.get(i).getCountryCode());
			}
		} else if (comboBoxAgeRange.getSelectedItem().equals("0 - 29")) {
			for ( int i = 0; i < recommendedYoung.size(); i++ ) {				
				modelRecommendedCities.addElement(recommendedYoung.get(i).getCityName() + " " +  recommendedYoung.get(i).getCountryCode());
			}					
		} else if (comboBoxAgeRange.getSelectedItem().equals("30 - 59")) {
			for ( int i = 0; i < recommendedMiddle.size(); i++ ) {				
				modelRecommendedCities.addElement(recommendedMiddle.get(i).getCityName() + " " +  recommendedMiddle.get(i).getCountryCode());
			}
		} else {
			for ( int i = 0; i < recommendedElder.size(); i++ ) {				
				modelRecommendedCities.addElement(recommendedElder.get(i).getCityName() + " " +  recommendedElder.get(i).getCountryCode());
			}
		}
	}

	private void makeRecommendations() {		
		if (recommendedYoung.isEmpty() && !chckbxCustomRecommendation.isSelected()) {
			recommendedYoung = pty.recommend(cities);				
		}
		if (recommendedMiddle.isEmpty() && !chckbxCustomRecommendation.isSelected()) {
			recommendedMiddle = ptm.recommend(cities);
		}
		if (recommendedElder.isEmpty() && !chckbxCustomRecommendation.isSelected()) {				
			recommendedElder = pte.recommend(cities);
		}
		if (recommendedCustom.isEmpty() && chckbxCustomRecommendation.isSelected()) {
			recommendedCustom = ptc.recommend(cities);
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

	@Override
	public void actionPerformed(ActionEvent e) {
		if (chckbxCustomRecommendation.isSelected()) {
			panelCustomSettings.setVisible(true);
		} else {
			panelCustomSettings.setVisible(false);
		}

	}

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
		/*
		System.out.println("");
		for (int i = 0; i < selections.length; i++) {
			System.out.println(selections[i]);
		}*/
	}


	private synchronized void addCity(String cityName, String countryCode) {
		SwingWorker<Void, Void> worker = new SwingWorker<>() {

			@Override
			protected Void doInBackground() throws Exception {
				Date date = new Date();
				City temp;

				try {
					temp = new City(cityName, countryCode, TERMS, OPEN_WEATHER_APPID_22046, FOURSQUARE_APPID_22046, date.getTime());
					if (!cities.contains(temp)) {
						cities.add(temp);
					} else {						
						JOptionPane.showMessageDialog(null, "A city with the name: " + temp.getCityName() + " and country code: " + temp.getCountryCode()  + " has already been added, skipping.", "Doublicate city found", JOptionPane.WARNING_MESSAGE);		
					}

				} catch (IOException e) {
					System.out.println(e.getMessage());
					//e.printStackTrace();
				} catch (InterruptedException e) {
					System.out.println(e.getMessage());
					//e.printStackTrace();
				} catch (AmadeusErrorException e) {
					System.out.println(e.getMessage());
					//e.printStackTrace();
				} catch (OpenWeatherCityNotFoundException e) {
					System.out.println(e.getMessage());
					//e.printStackTrace();
				} catch (FoursquareNoCityException e) {
					System.out.println(e.getMessage());
				}				
				return null;


			}

			@Override
			protected void done() {
				modelSelectedCities.removeAllElements();
				for (int i = 0; i < cities.size(); i++ ){
					modelSelectedCities.addElement(cities.get(i).getCityName() + " " +  cities.get(i).getCountryCode());
				}
				createCitySemaphoreDown();
			}			
		};
		worker.execute();


	}

	private synchronized void createCitySemaphoreUp() {
		this.createCitySemaphore++;
		this.btnRecommend.setEnabled(false);
		this.lblStatus.setText("Please wait, working...");

	}

	private synchronized void createCitySemaphoreDown() {
		this.createCitySemaphore--;
		if (this.createCitySemaphore == 0) {
			this.btnRecommend.setEnabled(true);
			this.lblStatus.setText("Ready");
		}
	}


	private void printResults(ArrayList<City> cities) {
		for (City c: cities) {
			System.out.println(c.getCityName());
			for (int i=0; i < c.getVectorRepresentation().length; i++) {
				System.out.printf(c.getVectorRepresentation()[i] + ", ");
			}
			System.out.println();
			System.out.println("Confirmed: "+ c.getCovidData().getData().getDiseaseCases().getConfirmed());
			System.out.println("Covid risk: " + c.getCovidData().getData().getDiseaseRiskLevel());
			System.out.println();
		}
	}


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
	}

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
			} else {
				citiesHashMap.get(key).add(city.getCityName());
			}
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
		System.out.print("Cities read from saved file are: ");
		for (City city: cities) {
			System.out.printf(city.getCityName() + " - ");
		}
		System.out.println("\n");
		return cities;    
	}
}   

