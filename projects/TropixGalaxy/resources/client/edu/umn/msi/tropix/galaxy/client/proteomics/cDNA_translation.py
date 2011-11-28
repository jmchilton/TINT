'''
    Reads DNA sequences from a file.
    Translates in the three forward frames or all six frames.
    Stop codons break a submitted sequence into sub-sequences
    Sub-seqeunces must be of <LENGTH-THRESHOLD> minimum length.
'''
import sys
import pdb

#==================================================================== Constants
CODON_RESIDUE = {   'GCT': 'A', 'GCC':'A', 'GCA': 'A', 'GCG': 'A',
                    'TGT': 'C', 'TGC': 'C',
                    'GAT': 'D', 'GAC': 'D',
                    'GAA': 'E', 'GAG': 'E',
                    'TTT': 'F', 'TTC': 'F',
                    'GGT': 'G', 'GGC': 'G', 'GGA': 'G', 'GGG': 'G',
                    'CAT': 'H', 'CAC': 'H',
                    'ATT': 'I', 'ATC': 'I', 'ATA': 'I',
                    'AAA': 'K', 'AAG': 'K',
                    'TTA': 'L', 'TTG': 'L', 'CTT': 'L', 'CTC': 'L', 'CTA': 'L', 'CTG': 'L',
                    'ATG': 'M',
                    'AAT': 'N', 'AAC': 'N',
                    'CCT': 'P', 'CCC': 'P', 'CCA':'P', 'CCG': 'P',
                    'CAA': 'Q', 'CAG': 'Q',
                    'CGT': 'R', 'CGC': 'R', 'CGA': 'R', 'CGG': 'R', 'AGA': 'R', 'AGG': 'R',
                    'TCT': 'S', 'TCC': 'S', 'TCA': 'S', 'TCG': 'S', 'AGT': 'S', 'AGC': 'S',
                    'ACT': 'T', 'ACC': 'T', 'ACA': 'T', 'ACG': 'T',
                    'GTT': 'V', 'GTC': 'V', 'GTA': 'V', 'GTG': 'V',
                    'TGG': 'W',
                    'TAT': 'Y', 'TAC': 'Y',
                    'TAA': '*', 'TGA': '*', 'TAG': '*' }

#====================================================================== Classes
class Sequence:
    ''' Holds protein sequence information '''
    def __init__(self):
        self.header = ""
        self.sequence = ""
   
class FASTAReader:
    """
        FASTA db iterator. Returns a single FASTA sequence object.
    """
    def __init__(self, fasta_name):
        self.fasta_file = open(fasta_name)
        
    def __iter__(self):
        return self
        
    def next(self):
        ''' Iteration '''
        while True:
            line = self.fasta_file.readline()
            if not line:
                raise StopIteration
            if line[0] == '>':
                break
        
        seq = Sequence()
        seq.header = line.rstrip().replace('\n','').replace('\r','')
       
        while True:
            tail = self.fasta_file.tell()
            line = self.fasta_file.readline()
            if not line:
                break
            if line[0] == '>':
                self.fasta_file.seek(tail)
                break
            seq.sequence = seq.sequence + line.rstrip().replace('\n','').replace('\r','')          
        return seq

class DNATranslator():
    '''
        Iterator class. Reads a fasta file of DNA sequences and translates into reading frames.
        Returns a translation for each DNA seqeunce.
        If c_dna is True, then reads only the three forward frames. Else, reads all 6 frames.

    '''
    def __init__(self, fasta_name, minimum_length=15, c_dna=True, trans_method='without_m'):
        self.fasta_reader = FASTAReader(fasta_name)
        self.forward_only = c_dna
        self.translation_method = trans_method
        self.minimum_length = minimum_length
    
    def __iter__(self):
        return self

    def next(self):
        '''
            The iteration stuff
        '''
        dna_seq = self.fasta_reader.next()
        return self.format_translation(self.translate_sequence(dna_seq.sequence),dna_seq.header)
    
    def format_translation(self, translated_seq, seq_id):
        '''
            Constructs a list of sequences from one translation.
            Each sequence is based in stop codon location and the 
            minimum length threshold.
        '''
        import re
        return_value = []
    
        translation_method = {  'with_m': lambda a_frame: [ x for x in re.split('(M[A-Z]*\*)', translated_seq['translation'][a_frame[1]]) if x.startswith('M') and len(x) > self.minimum_length ],
                                'without_m': lambda a_frame: [ x for x in re.split('([A-Z]*\*)', translated_seq['translation'][a_frame[1]]) if len(x) > self.minimum_length ]
                             }

        if self.forward_only:
            the_frames = (('Frame 1', 1), ('Frame 2', 2), ('Frame 3', 3))
        else:
            the_frames = (('Frame 1', 1), ('Frame 2', 2), ('Frame 3', 3), ('Frame 4', 4), ('Frame 5', 5), ('Frame 6', 6))

        for a_frame in the_frames:
            candidates = translation_method[self.translation_method](a_frame)
            try:
                return_value.append(seq_id + ' ' + a_frame[0] + '\n' + candidates[0])
            except:
                pass
        return return_value
    
    def translation_loop(self, start, a_seq):
        translation = ''
        idx = start
        while idx + 2 < len(a_seq):
            try:
                translation += CODON_RESIDUE[a_seq[idx:idx+3].upper()]
            except:
                pass
            idx += 3
        return translation

    def translate_sequence(self, a_sequence):
        '''
            Takes: dna seq
            Returns: dict of multi-frame protein translation
            Translation starts with ATG
        '''
        the_frames = ((1,0),(2,1),(3,2))
        translated_results = dict()
        
        forward_seq = a_sequence
        if not self.forward_only:
            rev_seq = self.translate_dna_codons(forward_seq)
            the_frames = ((1,0),(2,1),(3,2),(4,0),(5,1),(6,2))
        
        for a_pair in the_frames:
            if a_pair[0] < 4:
                translated_results[a_pair[0]] = self.translation_loop(a_pair[1], forward_seq)
            else:
                translated_results[a_pair[0]] = self.translation_loop(a_pair[1], rev_seq)
        return_value = {'translation': translated_results , 'errors': None }
        return return_value

    def translate_dna_codons(self, dna_seq_5_3):
        '''
            Given a 5' to 3' dna sequence, will reverse, then translate each codon.
            Returns translated sequence
        '''
        tr = {'A':'T','T':'A','G':'C','C':'G'}
        rev_seq = ''
        for codon in dna_seq_5_3[::-1]:
            try:
                rev_seq += tr[codon]
            except:
                pass
        return rev_seq
        

if __name__ == '__main__':
    import optparse
    parser = optparse.OptionParser()
    parser.add_option("--input")
    parser.add_option("--output")
    parser.add_option("--with_m", action="store_true", default=False)
    parser.add_option("--c_dna", action="store_true", default=False)
    (options, args) = parser.parse_args()
    
    if options.with_m:
        trans_method = "with_m"
    else:
        trans_method = "without_m"
    c_dna = options.c_dna 
    trans = DNATranslator(options.input, c_dna=c_dna, trans_method=trans_method)
    
    with open(options.output, "w") as f_out:
        for x in trans:
            for item in x:
                f_out.write(item[:-1])
                f_out.write('\n')

'''
NOTES:
    TODO: letter case for the dna codons needs to be looked at for safety.
'''
