package edu.umn.msi.tropix.proteomics.xtandem.impl;

import java.io.InputStream;
import java.io.OutputStream;

import javax.annotation.Nullable;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.springframework.util.StringUtils;

import edu.umn.msi.tropix.proteomics.bioml.Bioml;
import edu.umn.msi.tropix.proteomics.bioml.Note;
import edu.umn.msi.tropix.proteomics.pepxml.MsmsPipelineAnalysis.MsmsRunSummary.SampleEnzyme;
import edu.umn.msi.tropix.proteomics.pepxml.MsmsPipelineAnalysis.MsmsRunSummary.SampleEnzyme.Specificity;

class XTandemPepXmlWriterImpl {

  static class XTandemPepXmlWriterOptions {
    private OutputStream outputStream;
    private InputStream resultBioml;
    private Bioml parameters;
    
    XTandemPepXmlWriterOptions(final OutputStream outputStream, final InputStream resultBioml, final Bioml parameters) {
      this.outputStream = outputStream;
      this.resultBioml = resultBioml;
      this.parameters = parameters;
    }

  }

  private final XTandemPepXmlWriterOptions options;
  private XMLStreamWriter writer;

  XTandemPepXmlWriterImpl(final XTandemPepXmlWriterOptions options) {
    this.options = options;
  }

  void write() {
    try {
      writer = XMLOutputFactory.newInstance().createXMLStreamWriter(options.outputStream);
      writer.setDefaultNamespace("http://regis-web.systemsbiology.net/pepXML");
      writer.writeStartDocument();
      writer.writeStartElement("msms_pipeline_analysis");
      writer.writeDefaultNamespace("http://regis-web.systemsbiology.net/pepXML");
      writeRunSummary();
      writer.writeEndElement();
      writer.writeEndDocument();
      writer.flush();
      writer.close();
    } catch(XMLStreamException e) {
      throw new RuntimeException(e);
    }
  }

  private void writeEnzyme() throws XMLStreamException {
    final String cleavageSite = getParameter("protein, cleavage site");
    writeSampleEnzyme(PepXmlCleavageSites.getEnzymeFromCleavageSite(cleavageSite));
  }

  private void writeSampleEnzyme(final SampleEnzyme enzyme) throws XMLStreamException {
    writer.writeStartElement("sample_enzyme");
    writer.writeAttribute("name", enzyme.getName());
    for(Specificity specifity : enzyme.getSpecificity()) {
      writeSpecifity(specifity);
    }
    writer.writeEndElement();
  }

  private void writeSpecifity(final Specificity specificity) throws XMLStreamException {
    writer.writeStartElement("specificity");
    writeAttributeIfNotNull("cut", specificity.getCut());
    writeAttributeIfNotNull("no_cut", specificity.getNoCut());
    writer.writeAttribute("sense", specificity.getSense());
    writer.writeEndElement();
  }

  private void writeAttributeIfNotNull(final String attributeName, @Nullable final String attributeValue) throws XMLStreamException {
    if(attributeValue != null) {
      writer.writeAttribute(attributeName, attributeValue);
    }
  }

  private void writeRunSummary() throws XMLStreamException {
    writer.writeStartElement("msms_run_summary");
    writer.writeAttribute("base_name", "input");
    writer.writeAttribute("raw_data_type", "raw");
    writer.writeAttribute("raw_data", ".mzxml");
    writeEnzyme();
    writeSearchSummary();
    writer.writeEndElement();
  }

  private void writeSearchSummary() throws XMLStreamException {
    writer.writeStartElement("search_summary");
    writer.writeAttribute("search_engine", "X! Tandem");
    writeParameters();
    writer.writeEndElement();
  }

  private void writeParameters() throws XMLStreamException {
    for(Note note : options.parameters.getNote()) {
      writeParameter(note);
    }    
  }
  

  private void writeParameter(final Note note) throws XMLStreamException {
    final String label = note.getLabel();
    final String value = note.getValue();
    if(StringUtils.hasText(label) && StringUtils.hasText(value)) {
      writeParameter(label, value);
    }    
  }

  private void writeParameter(final String label, final String value) throws XMLStreamException {
    writer.writeStartElement("parameter");
    writer.writeAttribute("name", label);
    writer.writeAttribute("value", value);
    writer.writeEndElement();
  }
  
  private String getParameter(final String labelName) {
    String value = null;
    for(Note note : options.parameters.getNote()) {
      final String label = note.getLabel();
      if(label != null && label.equals(labelName)) {
        value = note.getValue();
        break;
      }
    }
    return value;
  }
  
}
