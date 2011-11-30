""" A script to build specific fasta databases """
import sys
import logging

#===================================== Iterator ===============================
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
#==============================================================================

def target_match(target, search_entry):
    ''' Matches '''
    for atarget in target:
        if search_entry.upper().find(atarget.upper()) > -1:
            return atarget
    return None
       

def main():
    ''' the main function'''
    logging.basicConfig(filename='filter_fasta_log', 
        level=logging.INFO,
        format='%(asctime)s :: %(levelname)s :: %(message)s',)

    used_sequences = set()
    work_summary = {'wanted': 0, 'found':0, 'duplicates':0}
    targets = []

    f_target = open(sys.argv[1])
    for line in f_target.readlines():
        targets.append(line.strip())
    f_target.close()

    logging.info('Read target file and am now looking for %d %s', len(targets), 'sequences.') 

    work_summary['wanted'] = len(targets)
    homd_db = FASTAReader(sys.argv[2])

    output = open(sys.argv[3], "w")
    try:
        for entry in homd_db:
            target_matched_results = target_match(targets, entry.header)
            if target_matched_results:
                work_summary['found'] += 1
                targets.remove(target_matched_results)
                if entry.sequence in used_sequences:
                    work_summary['duplicates'] += 1
                else:
                    used_sequences.add(entry.sequence)
                    output.write(entry.header)
                    output.write('\n')
                    output.write(entry.sequence)
                    output.write('\n')
    finally:
        output.close()
        
    logging.info('Completed filtering')
    for parm, count in work_summary.iteritems():
        logging.info('%s ==> %d', parm, count)

if __name__ == "__main__":
    main()
