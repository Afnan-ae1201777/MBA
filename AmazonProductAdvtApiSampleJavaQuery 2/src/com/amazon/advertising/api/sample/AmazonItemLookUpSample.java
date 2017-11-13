/**********************************************************************************************
 * Copyright 2009 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"). You may not use this file 
 * except in compliance with the License. A copy of the License is located at
 *
 *       http://aws.amazon.com/apache2.0/
 *
 * or in the "LICENSE.txt" file accompanying this file. This file is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under the License. 
 *
 * ********************************************************************************************
 *
 *  Amazon Product Advertising API
 *  Signed Requests Sample Code
 *
 *  API Version: 2009-03-31
 *
 */

package com.amazon.advertising.api.sample;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.sqlite.SQLiteException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/*
 * This class shows how to make a simple authenticated ItemLookup call to the
 * Amazon Product Advertising API.
 * 
 * See the README.html that came with this sample for instructions on
 * configuring and running the sample.
 */
public class AmazonItemLookUpSample {
	
	/*
	 * Your AWS Access Key ID, as taken from the AWS Your Account page.
	 */
	private static final String AWS_ACCESS_KEY_ID = "AKIAJJLG2TQWSAYT5LAA"; // "YOUR_ACCESS_KEY_ID_HERE";

	/*
	 * Your AWS Secret Key corresponding to the above ID, as taken from the AWS
	 * Your Account page.
	 */
	private static final String AWS_SECRET_KEY = "2dl6nmKYbVxoBttwdEgC3exMMkBgt7s3dB+OweNy"; // "YOUR_SECRET_KEY_HERE";

	/*
	 * Use one of the following end-points, according to the region you are
	 * interested in:
	 * 
	 * US: ecs.amazonaws.com CA: ecs.amazonaws.ca UK: ecs.amazonaws.co.uk DE:
	 * ecs.amazonaws.de FR: ecs.amazonaws.fr JP: ecs.amazonaws.jp
	 */
	private static final String ENDPOINT = "ecs.amazonaws.com";

	/*
	 * The Item ID to lookup. The value below was selected for the US locale.
	 * You can choose a different value if this value does not work in the
	 * locale of your choice.
	 */
	// private static final String ITEM_ID = "0545010225";
	private static final String ITEM_title = "pink cover for iphone 8";

	private final static SQLiteJDBCDriverConnection database =new SQLiteJDBCDriverConnection();
	private static Map<String,String>data=new HashMap<String,String>();
	private static Set<Map<String,String>>data2= new HashSet<Map<String,String>>();
	public static void main(String[] args) {
		/*
		 * Set up the signed requests helper
		 */
		SignedRequestsHelper helper;
		try {
			helper = SignedRequestsHelper.getInstance(ENDPOINT,
					AWS_ACCESS_KEY_ID, AWS_SECRET_KEY);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		String requestUrl = null;

		/* The helper can sign requests in two forms - map form and string form */

		/*
		 * Here is an example in map form, where the request parameters are
		 * stored in a map.
		 */
		System.out.println("Map form example:");
		Map<String, String> params = new HashMap<String, String>();
		params.put("Service", "AWSECommerceService");
		params.put("AssociateTag", "q09d6-20");
		params.put("Version", "2009-03-31");
		params.put("Operation", "ItemSearch");
		params.put("Keywords", ITEM_title);
		params.put("ResponseGroup", "Large");
		params.put("SearchIndex", "Electronics");
		requestUrl = helper.sign(params);
		System.out.println("Signed Request is \"" + requestUrl + "\"");

		fetchData(requestUrl);
		System.out.println();

		/*
		 * /* Here is an example with string form, where the requests parameters
		 * have already been concatenated into a query string. *
		 * System.out.println("String form example:"); String queryString =
		 * "Service=AWSECommerceService&Version=2009-03-31&Operation=ItemLookup&ResponseGroup=Small&ItemId="
		 * + ITEM_ID; requestUrl = helper.sign(queryString);
		 * System.out.println("Request is \"" + requestUrl + "\"");
		 * 
		 * title = fetchTitle(requestUrl); System.out.println("Title is \"" +
		 * title + "\""); System.out.println();
		 */
	}

	/*
	 * Utility function to fetch the response from the service and extract the
	 * title from the XML.
	 */
	private static String fetchTitle(String requestUrl) {
		String title = null;
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(requestUrl);
			Node titleNode = doc.getElementsByTagName("Title").item(0);
			title = titleNode.getTextContent();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return title;
	}

	private static void fetchData(String requestUrl) {
		System.out.format("%-5s\t%-20s\t%-20s\t%-20s\t%-20s\t%-20s\t%-30s\t%-30s\t%-20s",
				"Result", "ASIN", "SaleRank", "Brand", "Price","Color","Category", "Manufacturer",
				"Similar products asin");
		System.out.println("");
		System.out.println("");

		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(requestUrl);
			NodeList itemsNode = doc.getElementsByTagName("Item");
			int nodelength = itemsNode.getLength();
			if (nodelength > 100) {
				nodelength = 100;
			}
			for (int i = 0; i < nodelength; i++) {
				String asin = null, salerank = null, brand = null, price = null, manu = null,color=null,category=null;
				String similar[] = null;
				Node n = itemsNode.item(i);

				NodeList subitems = n.getChildNodes();

				for (int k = 0; k < subitems.getLength(); k++) {
					Node att = subitems.item(k);
					if (att.getNodeName().equals("ASIN")) {
						asin = att.getTextContent();
					}

					if (att.getNodeName().equals("SalesRank")) {
						salerank = att.getTextContent();
					} // end of if SalesRank
					else if (att.getNodeName().equals("ItemAttributes")) {
						NodeList attributeList = att.getChildNodes();

						for (int j = 0; j < attributeList.getLength(); j++) {
							Node br = attributeList.item(j);
							if (br.getNodeName().equals("Brand")) {
								brand = br.getTextContent();
							} 
							else if(br.getNodeName().equals("Color")){
								color = br.getTextContent();

							}else if(br.getNodeName().equals("Binding")){
								category = br.getTextContent();

							}
							else if (br.getNodeName().equals("ListPrice")) {
								NodeList priceList = br.getChildNodes();
								for (int z = 0; z < priceList.getLength(); z++) {
									Node pr = priceList.item(z);
									if (pr.getNodeName().equals(
											"FormattedPrice")) {
										price = pr.getTextContent();

									}
								}// end of for z

							}// end else if ListPrice
							else if (br.getNodeName().equals("Manufacturer")) {
								manu = br.getTextContent();
							}
						}// end of for j

					}// end of else if ItemAttributes
					else if (att.getNodeName().equals("SimilarProducts")) {
						NodeList similarList = att.getChildNodes();
						similar = new String[similarList.getLength()];

						for (int m = 0; m < similarList.getLength(); m++) {
							Node sm = similarList.item(m);
							
							if (sm.getNodeName().equals("SimilarProduct")) {
								NodeList sproduct = sm.getChildNodes();
								for (int v = 0; v < sproduct.getLength(); v++) {
									Node sr = sproduct.item(v);
									if (sr.getNodeName().equals("ASIN")) {
										similar[m] = sr.getTextContent();
										 //System.out.println(similar[v]);
									}// end if ASIN
								}// end of for v

							}// end of if SimilarProducts
						}// end of for m
					}// end of else if SimilarProducts

				}// end of for k
				data.put("ASIN", asin);
				data.put("saleRank", salerank);
				data.put("brand", brand);
				data.put("price", price);
				data.put("color", color);
				data.put("category", category);
				data.put("manufacturer", manu);
//				
//				
				System.out.format(
						"%d\t%-20s\t%-20s\t%-20s\t%-20s\t%-20s\t%-30s\t%-30s\t%20s", i, asin,
						salerank, brand, price,color,category, manu, Arrays.toString(similar));
				try{
				database.insert("Amazon_table", data);
				Map<String,String>similarMap= new HashMap<String,String>();
				for (String s: similar){
					similarMap.put("ASIN", asin);
					similarMap.put("similar_ASIN", s);
					database.insert("amazon_similar_product", similarMap);

				}
				
				}
				catch(SQLiteException ex){
					//System.out.println(ex.getErrorCode());
					if (ex.getErrorCode()!=19){
						throw ex;
					}
				}
		
				System.out.println("");
			} // end of for i
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}

