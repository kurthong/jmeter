/*
 * ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in
 * the documentation and/or other materials provided with the
 * distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 * if any, must include the following acknowledgment:
 * "This product includes software developed by the
 * Apache Software Foundation (http://www.apache.org/)."
 * Alternately, this acknowledgment may appear in the software itself,
 * if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 * "Apache JMeter" must not be used to endorse or promote products
 * derived from this software without prior written permission. For
 * written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 * "Apache JMeter", nor may "Apache" appear in their name, without
 * prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.jmeter.protocol.http.control.gui;

import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JCheckBox;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

import org.apache.jmeter.protocol.http.sampler.AccessLogSampler;
import org.apache.jmeter.samplers.gui.AbstractSamplerGui;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jmeter.gui.util.FilePanel;
import org.apache.jorphan.gui.JLabeledTextField;
import org.apache.jorphan.gui.layout.VerticalLayout;

/**
 * Title:		JMeter Access Log utilities<br>
 * Copyright:	Apache.org<br>
 * Company:		nobody<br>
 * License:<br>
 * <br>
 * Look at the apache license at the top.<br>
 * <br>
 * Description:<br>
 * So what is this log Sampler GUI? It is a sampler that
 * can take Tomcat access logs and use them directly. I
 * wrote a tomcat access log parser to convert each line
 * to a normal HttpSampler. This way, you can stress
 * test your servers using real production traffic. This
 * is useful for a couple of reasons. Some bugs are
 * really hard to track down, which only appear under
 * production traffic. Therefore it is desirable to use
 * the actual queries to simulate the same exact condition
 * to facilitate diagnosis.<p>
 * If you're working on a project to replace an existing
 * site, it is a good way to simulate the same exact
 * use pattern and compare the results. The goal here is
 * to get as close to apples to apples comparison as
 * possible. Running a select subset of queries against
 * a webserver normally catches a lot, but it won't give
 * an accurate picture of how a system will perform
 * under real requests.
 * <br>
 * Created on:  Jun 26, 2003
 *
 * @author Peter Lin
 * @version $Id$ 
 */
public class AccessLogSamplerGui
    extends AbstractSamplerGui
    implements ChangeListener
{

    private static final String label = JMeterUtils.getResString("log_sampler");
    JLabeledTextField parserClassName =
        new JLabeledTextField(JMeterUtils.getResString("log_parser"));
	JLabeledTextField generatorClassName =
		new JLabeledTextField(JMeterUtils.getResString("generator"));
	JLabeledTextField HOSTNAME =
		new JLabeledTextField(JMeterUtils.getResString("servername"));
	JLabeledTextField PORT =
		new JLabeledTextField(JMeterUtils.getResString("port"));
    FilePanel logFile =
        new FilePanel(JMeterUtils.getResString("log_file"), ".txt");
	private JCheckBox getImages;

	protected int PORTNUMBER = 80;
	
	public String DEFAULT_GENERATOR =
		"org.apache.jmeter.protocol.http.util.accesslog.StandardGenerator";
	public String DEFAULT_PARSER =
		"org.apache.jmeter.protocol.http.util.accesslog.TCLogParser";
    private AccessLogSampler SAMPLER = null;

    /**
     * This is the font for the note.
     */
    Font plainText = new Font("plain", Font.PLAIN, 10);

	JLabel noteMessage =
		new JLabel(JMeterUtils.getResString("als_message"));
	JLabel noteMessage2 =
		new JLabel(JMeterUtils.getResString("als_message2"));
	JLabel noteMessage3 =
		new JLabel(JMeterUtils.getResString("als_message3"));

    public AccessLogSamplerGui()
    {
        init();
    }

    /**
     * @see JMeterGUIComponent#getStaticLabel()
     */
    public String getStaticLabel()
    {
        return label;
    }

    /**
     * @see JMeterGUIComponent#createTestElement()
     */
    public TestElement createTestElement()
    {
    	if (SAMPLER == null){
    		System.out.println("the sampler was null, therefore we create a new one");
			SAMPLER = new AccessLogSampler();
			SAMPLER.setSamplerGUI(this);
			this.configureTestElement(SAMPLER);
			SAMPLER.setParserClassName(parserClassName.getText());
			SAMPLER.setGeneratorClassName(generatorClassName.getText());
			SAMPLER.setLogFile(logFile.getFilename());
			SAMPLER.setDomain(HOSTNAME.getText());
			SAMPLER.setPort(getPortNumber());
    	}
		return SAMPLER;
    }

	/**
	 * Utility method to parse the string and get a int port
	 * number. If it couldn't parse the string to an integer,
	 * it will return the default port 80.
	 * @return
	 */
	public int getPortNumber(){
		try {
			int port = Integer.parseInt(PORT.getText());
			return port;
		} catch (NumberFormatException exception){
			exception.printStackTrace();
			return 80;
		}
	}

    /**
     * Modifies a given TestElement to mirror the data in the gui components.
     * @see JMeterGUIComponent#modifyTestElement(TestElement)
     */
    public void modifyTestElement(TestElement s)
    {
		SAMPLER = (AccessLogSampler) s;
		SAMPLER.setSamplerGUI(this);
        this.configureTestElement(SAMPLER);
		SAMPLER.setParserClassName(parserClassName.getText());
		SAMPLER.setGeneratorClassName(generatorClassName.getText());
		SAMPLER.setLogFile(logFile.getFilename());
		SAMPLER.setDomain(HOSTNAME.getText());
		SAMPLER.setPort(getPortNumber());
		if (getImages.isSelected()){
			SAMPLER.setImageParser(true);
		} else {
			SAMPLER.setImageParser(false);
		}
    }

    /**
     * init() adds soapAction to the mainPanel. The class
     * reuses logic from SOAPSampler, since it is common.
     */
    private void init()
    {
        this.setLayout(
            new VerticalLayout(5, VerticalLayout.LEFT, VerticalLayout.TOP));

        // MAIN PANEL
        JPanel mainPanel = new JPanel();
        Border margin = new EmptyBorder(10, 10, 5, 10);
        mainPanel.setBorder(margin);
        mainPanel.setLayout(new VerticalLayout(5, VerticalLayout.LEFT));

        // TITLE
        JLabel panelTitleLabel = new JLabel(label);
        Font curFont = panelTitleLabel.getFont();
        int curFontSize = curFont.getSize();
        curFontSize += 4;
        panelTitleLabel.setFont(
            new Font(curFont.getFontName(), curFont.getStyle(), curFontSize));
        mainPanel.add(panelTitleLabel);
        // NAME
        mainPanel.add(getNamePanel());
        mainPanel.add(HOSTNAME);
        mainPanel.add(PORT);

        mainPanel.add(parserClassName);
        mainPanel.add(generatorClassName);
        mainPanel.add(logFile);
        HOSTNAME.addChangeListener(this);
        PORT.addChangeListener(this);
        logFile.addChangeListener(this);
        parserClassName.addChangeListener(this);
        generatorClassName.addChangeListener(this);

		// RETRIEVE IMAGES
		JPanel retrieveImagesPanel = new JPanel();
		getImages =
			new JCheckBox(
				JMeterUtils.getResString("web_testing_retrieve_images"));
		retrieveImagesPanel.add(getImages);
		mainPanel.add(retrieveImagesPanel);
		mainPanel.add(noteMessage);
		mainPanel.add(noteMessage2);
		mainPanel.add(noteMessage3);

        this.add(mainPanel);
    }

    /**
     * the implementation loads the URL and the soap
     * action for the request.
     */
    public void configure(TestElement el)
    {
        super.configure(el);
		SAMPLER = (AccessLogSampler) el;
		if (SAMPLER.getParserClassName().length() > 0){
			parserClassName.setText(SAMPLER.getParserClassName());
		} else {
			parserClassName.setText(this.DEFAULT_PARSER);
		}
        if (SAMPLER.getGeneratorClassName().length() > 0){
			generatorClassName.setText(SAMPLER.getGeneratorClassName());
        } else {
			generatorClassName.setText(this.DEFAULT_GENERATOR);
        }
        logFile.setFilename(SAMPLER.getLogFile());
        HOSTNAME.setText(SAMPLER.getDomain());
        PORT.setText(String.valueOf(SAMPLER.getPort()));
        getImages.setSelected(SAMPLER.isImageParser());
    }
    
    /**
     * stateChanged implements logic for the text field
     * and file chooser. When the value in the widget
     * changes, it will call the corresponding method to
     * create the parser and initialize the generator.
     */
    public void stateChanged(ChangeEvent event)
    {
        if (event.getSource() == parserClassName)
        {
            SAMPLER.instantiateParser();
        }
        if (event.getSource() == logFile)
        {
            //this.setUpGenerator();
        }
        if (event.getSource() == generatorClassName){
			SAMPLER.instantiateGenerator();
        }
        if (event.getSource() == HOSTNAME){
        }
        if (event.getSource() == PORT){
        }
    }

}
