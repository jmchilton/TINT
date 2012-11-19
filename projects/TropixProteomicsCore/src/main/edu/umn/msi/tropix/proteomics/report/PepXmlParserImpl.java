package edu.umn.msi.tropix.proteomics.report;

import java.io.InputStream;


import com.google.common.collect.Lists;
import com.google.common.collect.UnmodifiableIterator;

import edu.umn.msi.tropix.proteomics.conversion.impl.XMLStreamReaderUtils;
import edu.umn.msi.tropix.proteomics.xml.AbstractXmlIterator;

public class PepXmlParserImpl implements SearchReportParser {

  /*
   * <spectrum_query spectrum="dataset_200.dat.01635.01635.2" start_scan="1635" end_scan="1635" precursor_neutral_mass="804.5025" assumed_charge="2"
   * index="1">
   * <search_result>
   * <search_hit hit_rank="1" peptide="KVSSLSGK" peptide_prev_aa="R" peptide_next_aa="T" protein="ENSRNOP00000060519" protein_descr=
   * "pep:known chromosome:RGSC3.4:3:106413764:106474707:1 gene:ENSRNOG00000006378 transcript:ENSRNOT00000067865 gene_biotype:protein_coding transcript_biotype:protein_coding"
   * num_tot_proteins="4" num_matched_ions="12" tot_num_ions="14" calc_neutral_pep_mass="804.4707" massdiff="0.032" num_tol_term="2"
   * num_missed_cleavages="0" is_rejected="0">
   * <alternative_protein protein="ENSRNOP00000008528" protein_descr=
   * "pep:known chromosome:RGSC3.4:3:106413764:106474707:1 gene:ENSRNOG00000006378 transcript:ENSRNOT00000008528 gene_biotype:protein_coding transcript_biotype:protein_coding"
   * num_tol_term="2" peptide_prev_aa="R" peptide_next_aa="T"/>
   * <alternative_protein protein="ENSRNOP00000061447" protein_descr=
   * "pep:known chromosome:RGSC3.4:3:106413764:106474707:1 gene:ENSRNOG00000006378 transcript:ENSRNOT00000064395 gene_biotype:protein_coding transcript_biotype:protein_coding"
   * num_tol_term="2" peptide_prev_aa="R" peptide_next_aa="T"/>
   * <alternative_protein protein="ENSRNOP00000060349" protein_descr=
   * "pep:known chromosome:RGSC3.4:3:106413764:106470823:1 gene:ENSRNOG00000006378 transcript:ENSRNOT00000064345 gene_biotype:protein_coding transcript_biotype:protein_coding"
   * num_tol_term="2" peptide_prev_aa="R" peptide_next_aa="T"/>
   * <search_score name="hyperscore" value="366"/>
   * <search_score name="nextscore" value="348"/>
   * <search_score name="bscore" value="1"/>
   * <search_score name="yscore" value="1"/>
   * <search_score name="cscore" value="0"/>
   * <search_score name="zscore" value="0"/>
   * <search_score name="ascore" value="0"/>
   * <search_score name="xscore" value="0"/>
   * <search_score name="expect" value="14"/>
   * <analysis_result analysis="peptideprophet">
   * <peptideprophet_result probability="0.0722" all_ntt_prob="(0.0005,0.0006,0.0722)">
   * <search_score_summary>
   * <parameter name="fval" value="0.8091"/>
   * <parameter name="ntt" value="2"/>
   * <parameter name="nmc" value="0"/>
   * <parameter name="massd" value="0.032"/>
   * </search_score_summary>
   * </peptideprophet_result>
   * </analysis_result>
   * </search_hit>
   * </search_result>
   * </spectrum_query>
   */
  private class SpectrumReportIterator extends AbstractXmlIterator<SpectrumReport> {

    protected SpectrumReportIterator(InputStream inputStream) {
      super(inputStream, "spectrum_query");
    }

    protected SpectrumReport parseNext() {
      final String name = getAttributeValue("spectrum");
      final int startScan = Integer.parseInt(getAttributeValue("start_scan"));
      final int endScan = Integer.parseInt(getAttributeValue("end_scan"));
      final short charge = Short.parseShort(getAttributeValue("assumed_charge"));

      XMLStreamReaderUtils.skipToElement(reader(), "search_result");
      XMLStreamReaderUtils.skipToElement(reader(), "search_hit");
      final String peptideSequest = getAttributeValue("peptide");
      final String proteinName = getAttributeValue("protein");

      XMLStreamReaderUtils.skipToElement(reader(), "peptideprophet_result");
      final double peptideProbability = Double.parseDouble(getAttributeValue("probability"));
      final SpectrumMatch match = new SpectrumMatch(peptideSequest, proteinName, peptideProbability);
      final SpectrumReport report = new SpectrumReport(name, startScan, endScan, charge, Lists.newArrayList(match));
      return report;
    }
  }

  public UnmodifiableIterator<SpectrumReport> parse(final InputStream inputStream) {
    return new SpectrumReportIterator(inputStream);
  }

}
