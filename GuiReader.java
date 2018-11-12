import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.JButton;
import javax.swing.JTextArea;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.SystemColor;
import javax.swing.JScrollPane;

public class GuiReader {

	private JFrame frame;
	private JTextField textField;
	private JTextField textField_1;
	public JSONArray jsnArr;
	
	public JSONArray getJsnArr() {
		return jsnArr;
	}

	public void setJsnArr(JSONArray jsnArr) {
		this.jsnArr = jsnArr;
	}
	
	public JSONObject makeHttpRequest(String url,String method,List<NameValuePair>params) {
		InputStream is = null;
		String json = "";
		JSONObject jObj = null;
		
		try {
			if(method == "POST") {
				DefaultHttpClient httpClient = new DefaultHttpClient();
				HttpPost httpPost = new HttpPost(url);
				httpPost.setEntity(new UrlEncodedFormEntity(params));
				
				HttpResponse httpResponse = httpClient.execute(httpPost);
				HttpEntity httpEntity = httpResponse.getEntity();
				is = httpEntity.getContent();
			}else if(method == "GET") {
				DefaultHttpClient httpClient = new DefaultHttpClient();
				String paramString = URLEncodedUtils.format(params, "utf-8");
				url += "?" + paramString;
				HttpGet httpGet = new HttpGet(url);
				
				HttpResponse httpResponse = httpClient.execute(httpGet);
				HttpEntity httpEntity = httpResponse.getEntity();
				is = httpEntity.getContent();
			}
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"),8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			
			while ((line = reader.readLine())!=null) {
				sb.append(line +  "\n");
			}
			is.close();
			json = sb.toString();
			jObj = new JSONObject(json);
		}catch (JSONException e) {
			/*try {
				JSONArray jsnArr = new JSONArray(json);
				jObj = jsnArr.getJSONObject(0);
				setJsnArr(jsnArr);
			}catch(JSONException ee) {
				ee.printStackTrace();
			}*/
		}catch (Exception e) {
			e.printStackTrace();
		}
		return jObj;
		
	}
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GuiReader window = new GuiReader();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public GuiReader() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	
	
	
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 680, 454);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JLabel lblType = new JLabel("Category Id: ");
		lblType.setBounds(10, 37, 82, 14);
		frame.getContentPane().add(lblType);
		
		JLabel lblStatus = new JLabel("Category Name:");
		lblStatus.setBounds(10, 62, 84, 14);
		frame.getContentPane().add(lblStatus);
		
		JLabel lblJikanApi = new JLabel("The Meal ");
		lblJikanApi.setBounds(187, 11, 109, 14);
		frame.getContentPane().add(lblJikanApi);
		
		textField = new JTextField();
		textField.setBounds(139, 34, 180, 20);
		frame.getContentPane().add(textField);
		textField.setColumns(10);
		
		textField_1 = new JTextField();
		textField_1.setBounds(139, 59, 180, 20);
		frame.getContentPane().add(textField_1);
		textField_1.setColumns(10);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 112, 617, 292);
		frame.getContentPane().add(scrollPane);
		
		JTextArea textArea = new JTextArea();
		scrollPane.setViewportView(textArea);
		textArea.setBackground(SystemColor.inactiveCaption);
		
		JButton btnSearch = new JButton("Search");
		btnSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Thread thread1 = new Thread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						List<NameValuePair> params = new ArrayList<NameValuePair>();
						//params.add(new BasicNameValuePair("idCategory", textField.getText()));
						//params.add(new BasicNameValuePair("strCategory", textField_1.getText()));
						
						String id = textField.getText();
						String cat = textField_1.getText();
						String strUrl = "https://www.themealdb.com/api/json/v1/1/latest.php";
						JSONObject jsnObj = makeHttpRequest(strUrl,"GET",params);
						
						
						try {
							
							jsnArr = jsnObj.getJSONArray("meals");
							String strSetText = "";
							StringBuilder sb = new StringBuilder();
							
							for (int i = 0; i < jsnArr.length(); i++) 
							{
								
								
									JSONObject result = jsnArr.getJSONObject(i);
									String searchNameCategory,searchDescprition,searchid,searchMeal= "";
									searchid = result.optString("idCategory");
									searchNameCategory = result.optString("strCategory");
									searchMeal = result.optString("strMeal");
									searchDescprition = result.optString("strInstructions");
									
									if(searchNameCategory.equalsIgnoreCase(cat)||searchid.equalsIgnoreCase(id))
									{
										strSetText = " Meal Name : "+ searchMeal+"|| Description:"+ searchDescprition;
										
									sb.append(strSetText+ "\n");
									System.out.println(sb.toString());
									}
							}
								textArea.setText(sb.toString());
								
						}catch (JSONException e) {
							// TODO: handle exception
							e.printStackTrace();
						}catch (Exception e) {
							// TODO: handle exception
							e.printStackTrace();
						}
					}
				});
				thread1.start();
			}
		});
		btnSearch.setBounds(345, 58, 89, 23);
		frame.getContentPane().add(btnSearch);
		
		
	}
}
