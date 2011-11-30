'''
    Reads a proteinpilot comparison output and filters for significant
    proteins. 
'''
import sys
import pdb
import string
#---------------------------------------------------------------------- Classes
class RunSignificant(object):
    '''
        Class to hold protien significance by run.
    '''
    def __init__(self):
        self.run_id = dict()
        self.run_up = dict()
        self.run_down = dict()
        self.cutoff = 0.05
        self.current_run = ''

    def find_significance(self, data, header, index):
        ''' casts to float and then compares significance '''
        for idx in index:
            try:
                if float(data[idx]) < self.cutoff:
                    if float(data[idx - 1]) < 1.0:
                        if header[idx - 1] in self.run_down[self.current_run]:
                            pass
                        else:
                            self.run_down[self.current_run][header[idx - 1]] = []
                        self.run_down[self.current_run][header[idx - 1]].append(vals[0])
                    if float(data[idx - 1]) > 1.0:
                        if header[idx - 1] in self.run_up[self.current_run]:
                            pass
                        else:
                            self.run_up[self.current_run][header[idx - 1]] = []
                        self.run_up[self.current_run][header[idx - 1]].append(vals[0])
            except:
                pass
            
    def parse_data_line(self, vals, data_header, significant_index):
        ''' reads the data line and looks for siginificant proteins '''
        run_id = data_header.index('File Name')
        n_num = data_header.index('N')
        if vals[run_id] in self.run_id:
            if vals[n_num] in self.run_id[vals[run_id]]:
                return
        else:
            self.run_id[vals[run_id]] = set()
            self.run_up[vals[run_id]] = dict()
            self.run_down[vals[run_id]] = dict()

        self.run_id[vals[run_id]].add(vals[n_num])  
        self.current_run = vals[run_id]
        self.find_significance(vals, data_header, significant_index)

class VirtualProteins(object):
    '''
        Class to hold all virtual protein information for significant
        virtual proteins.
    '''
    def __init__(self):
        self.significant_runs = dict()
        self.cutoff = 0.05
        self.accessions = set()
        self.names = set()
    
    def find_significance(self, data, header, index, run):
        ''' casts to float and then compares significance '''
        for idx in index:
            try:
                if float(data[idx]) < self.cutoff:
                    self.significance = True
                    if float(data[idx - 1]) < 1.0:
                        self.significant_runs[run]['down'].append(header[idx - 1])
                    if float(data[idx - 1]) > 1.0:
                        self.significant_runs[run]['up'].append(header[idx - 1])
            except:
                pass


    def parse_data_line(self, vals, header_line, ion_index):
        ''' reads the data line and looks for significance '''
        self.accessions.add(vals[header_line.index('Common Accessions')])
        self.names.add(vals[header_line.index('Name')])
        run_id = header_line.index('File Name')
        if vals[run_id] in self.significant_runs:
            #we have already seen this protein
            pass
        else:
            self.significant_runs[vals[run_id]] = { 'up': [], 'down': [] }
        self.find_significance(vals, header_line, ion_index, vals[run_id])
            
class HtmlOutputDetail(object):
    '''
        Holds the details of the html output. That is, multiple tables
        captions, legends, text etc.
    '''

    def __init__(self):
        self.dict_of_tables = dict()
        self.intro_text = None
        self.experiment_index = None
        self.ions_of_interest = None

#-------------------------------------------------------------------- Functions
def clean_entry(a_list):
    ''' Removes white space from list entries. '''
    return_value = []
    for item in a_list:
        return_value.append(item.strip())
    return return_value

def extract_ion_ids(a_list):
    tmp = set()
    for item in a_list:
        if item.startswith('PVal'):
            tmp.add(item.split()[1])
    return list(tmp)


def build_ion_by_protein(virtual_protein_dict, ions_of_interest, run_idx):
    ret_str = "<tr><th>Virtual Protein ID</th><th>Direction</th>"
    for reporter_ion in ions_of_interest:
        ret_str += "<th>"
        ret_str += reporter_ion
        ret_str += "</th>"
    ret_str += "<th>Total Count</th>"
    ret_str += "</tr>"
    
    table_vals = dict()
    vpid_accessions = dict()

    for vp_id, vp in virtual_protein_dict.iteritems():
        vpid_accessions[vp_id] = ''
        for set_item in vp.accessions:
            vpid_accessions[vp_id] += set_item

        if len(vp.significant_runs) > 1:
            #table_vals[vp_id] = { x: {'up':[], 'down':[]} for x in ions_used }
            table_vals[vp_id] = dict( (x, {'up':[], 'down':[]}) for x in ions_used )
            for a_run, data in vp.significant_runs.iteritems():
                for an_ion in table_vals[vp_id].keys():
                    if an_ion in data['up']:
                        table_vals[vp_id][an_ion]['up'].append(a_run)
                    if an_ion in data['down']:
                        table_vals[vp_id][an_ion]['down'].append(a_run)
            
            down_count = [len(xx) for xx in [ x['down'] for x in table_vals[vp_id].values() ]]
            up_count =  [len(xx) for xx in [ x['up'] for x in table_vals[vp_id].values() ]]
            table_vals[vp_id]['up_count'] = sum(up_count)
            table_vals[vp_id]['down_count'] = sum(down_count)
            table_vals[vp_id]['total_count'] = sum(up_count) + sum(down_count)
            if sum(down_count) + sum(up_count) == 0:
                del table_vals[vp_id]
        
    #Sort so the highest total score VP is first in the print out
    tmp1 = sorted([(v['total_count'],k) for k,v in table_vals.iteritems()])[::-1]
    sorted_keys = [x[1] for x in tmp1]
    
    #for vp_id in table_vals.keys():
    for vp_id in sorted_keys:
        ret_str += '<tr></tr><tr><td rowspan=2 rel="#' + vp_id + '">'
        ret_str += vp_id
        ret_str += '</td><td>Up</td>'
        for an_ion in ions_used:
            ret_str += '<td title="'
            if table_vals[vp_id][an_ion]['up']:
                tmp_run_id = ''
                for a_run in table_vals[vp_id][an_ion]['up']:
                    ret_str += a_run + ','
                    tmp_run_id += run_index[a_run] + ', '
                tmp_run_id = tmp_run_id[:-2]
                ret_str = ret_str[:-1]
                ret_str += '">' + tmp_run_id
            else:
                ret_str += '">'
            ret_str += "</td>"
        ret_str += "<td>" + str(table_vals[vp_id]['up_count']) + "</td>"
        ret_str += "</tr>"
    
        ret_str += "<tr class='down_row'><td>Down</td>"
        for an_ion in ions_used:
            ret_str += '<td title="'
            if table_vals[vp_id][an_ion]['down']:
                tmp_run_id = ''
                for a_run in table_vals[vp_id][an_ion]['down']:
                    ret_str += a_run + ', '
                    tmp_run_id += run_index[a_run] + ', '
                ret_str = ret_str[:-2]
                tmp_run_id = tmp_run_id[:-2]
                ret_str += '">' + tmp_run_id
            else:
                ret_str += '">'
            ret_str += "</td>"
        ret_str += "<td>" + str(table_vals[vp_id]['down_count']) + "</td>"
        ret_str += "</tr>"
    return ret_str

def build_ion_by_run(up_dict, down_dict, ions_of_interest, run_idx):
    '''
        Returns an HTML string of rows for a table
        In addition returns data for building overlays for each
        set of proteins significant in a reporter ion.
    '''
    import string
    import random
    
    overlay_details = dict() #return structure for overlay building.

    ret_str = '<tr><th>Experiment Run ID</th><th>Direction</th>'
    for ion in ions_of_interest:
        ret_str += "<th>"
        ret_str += ion
        ret_str += "</th>"
    ret_str += "</tr>"
    
    runs = up_dict.keys()
    for run in runs:
        ret_str += '<tr></tr><tr><td rowspan=2 title="'
        ret_str += run + '">'
        ret_str += run_idx[run]
        ret_str += "</td><td>Up</td>"

        for ion in ions_of_interest:
            rnd_id = ''.join(random.choice(string.letters) for i in xrange(20))
            overlay_details[rnd_id] = []
            ret_str += '<td rel="#' + rnd_id + '">'
            try:    
                for avp in up_dict[run][ion]:
                    overlay_details[rnd_id].append(avp)
                ret_str += str(len(up_dict[run][ion]))
            except:
                ret_str += '0'
            ret_str += '</td>'
            
        ret_str += "</tr>"

        ret_str += "<tr class='down_row'><td>Down</td>"
        for ion in ions_of_interest:
            rnd_id = ''.join(random.choice(string.letters) for i in xrange(20))
            overlay_details[rnd_id] = []
            ret_str += '<td rel="#' + rnd_id + '">'
            try:
                for avp in down_dict[run][ion]:
                    overlay_details[rnd_id].append(avp)
                ret_str += str(len(down_dict[run][ion]))
            except:
                ret_str += '0'
            ret_str += "</td>"
        ret_str += "</tr>"

    return (ret_str, overlay_details)

def build_html_report(html_object):
    '''
    Build HTML page with table summarizing significant proteins.
'''
    header_str = """
<!DOCTYPE html>
<html lang="en">

<head>
  <meta charset="utf-8" />
  <script src="http://cdn.jquerytools.org/1.2.5/jquery.tools.min.js"></script>
	<style type="text/css">
		article,aside,figure,footer,header,hgroup,nav,section,table{display:block}
		html{background:white;color:black}
		body{
            font:normal medium 'Gill Sans','Gill Sans MT','Goudy Bookletter 1911','Linux Libertine O','Liberation Serif',Candara,serif;
            padding:0 ;margin:10 auto ;width:96em;line-height:1.75;word-spacing:0.1em}

        table {font-size: 14px;background: #fff;margin: 45px;border-collapse: collapse;}
        th {text-align: center;font-size: 16px;font-weight: bold;padding: 10px 8px;border-bottom: 2px double;}
        td {text-align: center;border-bottom: 1px solid #ccc;padding: 6px 8px;}
        td[rel] { cursor: pointer; }
        h2[rel] { cursor: pointer; }
        .down_row  { background: #CCCCCC; }

        a:link { color:#ffffff}
        a:visited { color:#ffffff}
        a:hover { color: #DFE670 }

.overlay_virtualprotein {
    
    /* must be initially hidden */
    display:none;
    
    /* place overlay on top of other elements */
    z-index:10000;
    
    /* styling */
    background-color:#333;
    font-size:12px;
    color:#fff;
    
    width:600px;    
    min-height:600px;
    border:1px solid #666;
    
    /* CSS3 styling for latest browsers */
    -moz-box-shadow:0 0 90px 5px #000;
    -webkit-box-shadow: 0 0 90px #000;  
}

.overlay_virtualprotein p, h2, h3 {
    margin: 10px;
}

.overlay_virtualprotein h2 {
    color:#aba;
    font-size:15px;
}

.overlay_virtualprotein h3 {
    
    color:#aba;
    font-size:14px;
}

/* close button positioned on upper right corner */
.overlay_virtualprotein .close {
    
    background-image: url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACQAAAAkCAYAAADhAJiYAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAB25JREFUeNqkWAtQVFUY/neX5bECLkgIwvCQkCAbn2kiKmq+MWNKccjSEQVNAgzKzEGnoVIHUghiIJlJwBeaMoFIwFAjOjqupiIiEimEPKRQWBVddpe9nXM8dzt79y6inplv7r3nnnPud///P//jSDiOg6E2CWrso+DKNo69coN8xHRJAKvnICIGKXPPksEw8PdoumEo5J5JSEBESiFjwD/zY3giGAMCkHdoSY7y4oZMiFGNlIEVhVxw5ckBQ0hPoaPQMzBQYqLSkgj7RKTCf9yawgbBll5ZUjyhAUpCS9GPoGGe9XSMgVGruIQYMrxU5BT443YICgx7e/thGRkZoRMmTJjk5eUVZGNj44D6XLu7u2/rdLrHLS0tdWfOnFFt2bLlIhr/mOIJRT8jNTAjhSXEg5KRUUkMQ3BGGIXgjzDR19d3Xm1tbWlPT88dbgits7OzoaysLB3NDUZ4A8EXYSTCcPqDcmZjmKqMSoeVjA2ViAOCY35+/uLw8PAYBwcHNzz+Un0DnP7jKlyoqwf1o0fQ3vUvBPn5goNCAdPGjYVF06eBx8hXyNrt7e2Xt27dmlpYWNiIHh8gPKLS0gqMnmMJ8TvGSkBGeePGjR2BgYGL8LjztXXwXcFhuNn89zPdxdJZIRC78n1CrK+vrzs5OfmLvXv3XkGvehH6GBUaSRFCjHRk1GDtKBmn6urq9XPmzFlvMBgIkfyScnieprC1hbTEWJg5aTzo9XpNbGxsTG5u7jVKSigpA0uI3008GWVOTs6imJiYb/HC8bvTofrCJXjRtjN+A5HY/fv3WwICAtaiDdCFutVUUhp+90kZ98+Tsqak7JHNrMEvvz90DKpVl8R99RCxPTsPahubwNnZ2aempmYrXp9qw5p+l4yUCtTF248dmrTW1dV1DLaVvOJSk78dF+APydFrQCqVWpTIppXvwew3JxqfdXo9bMv6EbDqkT3OS0hICGJ2Gu/1gZWQjPU5/v7+04h0Dv+MFuGMv+qiVMK+7VsgYsHbsDNuAyIlMxNHwqoI2Lg8HPYkxYGfp6exv6XjLhyvPk0+GBUVtVwgIbL92V/kvbJ1YmJioJub22utd7vgzOWrgGXI455aDT8UnSATlswIhl1xMSCTSY3vN69aAevCl5L3B8oq4HZ7u8n8w+VV5J2Hh8dYS4QkgnglR7tq/NMtfl1UHQWl5ZCaf4jcL0akdn4SQ9SX8MEKiKJkfvqlDPYUHjGb29R6Bzq774GTk5NXRESEpyAeSqwYlRmjuZ+fXyDurPvrtoV0B5P6lVw/Wx1JSAX4eiP1eFAypxCZIotzsV26u4yA+fPnBxQVFTUwsdAklhkJobjkhDvu9aotrfmU1Mn/SRnJlCAyB44MOu+fnh5ydXd3dxHkVBJL+RBhPGA0Zstt5Ahnk2cX5XBi6Hg3DZJpWXwjFcv0NBrNQ9yhtB82qHtJ/DACPgpbSCZfufnn03Axczp8vWkdyJBNWZo33H4YGatWqx8w3yUcpILEimR6HR0d2HjA39vTIptPEZk17ywmkw+eqoTV27+BNBRaeFIpiJQU7T6xuf5enmTcuXPnbjH5EcdKiM30dCqVqhZ3ThkbJMomPnKFkQy2o937D5H+gpMV9J6S+ni9mZ/CfgzbG9KCOjMzs0mQsBFCnJBQUlKSqre3t23cmFfR5FEmOnZydEAfCyb3+1GgTSsw3dpYWjyp2ZMngJebq8n78DkzyRVpoZ5Gei1DiLMSpJ56OugJmnBdqVR6xkcuh7jUDOOCPQ8fwtqvdsGC4Cmw70SpqH0eLK8E3YAeriO30dJ51zgG2w5vc1VVVSdpUNWxauOjPR/HbGnQU4aEhIyuqKjYr1AonGNRpK+5XAsv21I2RsGy0BBobW1VeXt7x2PPQhO2Pj4vErMhLMInZ8+e7bh48WIJfrkLeeIxyBBfIthD5MK5hMzAwIA2JSUlk0k7tGzGaIkQHtgXGhq6r6mpqcZeYQc525IA29SLtLXvLoHPkfPE7dixY2l5eXkNjFR0THkEYimsnKoOOwtHbMfNzc2pPj4+b+EUIvd4CRSUVUK/VvtMIh6uLrAZxbd5UyeT54aGhvKgoKAdNDF7QKsRrZkNCZJ8vurgSfF59Xacx+Cx3SikHK36HSrOq0hKYeJpkUOcHBhAjH7ZrOkgt7KC/v7+BwUFBanR0dEVlMhDhoyOVZmwDJIySRomgssGbwTskKZmZ2d/2dbWdlVY7ty6087V32rmuu7dNyuFGhsbfwsLCwvHZRQtp7AfcaJFhJwpxc0rV6ZQ5DM4ayaltaMSU2RlZc1AKcpclNO87ujoaOKo0HqGrq6uBqTma0ePHq1MT0+/Qe2FLxY1IhUsN5RSmk1rrRk18mW0DZ9cxcXFjUbEbOvr67uLi4u7mFJaQ9HPgDViThDLzAmJkJIIDhrkTJbH5sMSocdnoGX6DAxAWNtLLB3XCE5AJCJHMVYCMmLHMXqxIxn2OEZ4YCV51gkaQ0wiICcV9IFI5mAQHl4JD62e+wSNmczRwyYQOGGwcIIGlkgM1v4TYAAi8nNfsug40QAAAABJRU5ErkJggg==);
    position:absolute;
    right:-15px;
    top:-15px;
    cursor:pointer;
    height:35px;
    width:35px;
}

.tooltip {
	display:none;
    border:1px solid #fff;
	font-size:12px;
	padding:10px;
	color:#ffffff;
    background-color: #616365;
}

/* root element for scrollable */
.vertical {  
    
    /* required settings */
    position:relative;
    overflow:hidden;    

    /* vertical scrollers have typically larger height than width */    
    height: 665px;   
    width: 600px;
    border-top:1px solid #ddd;  
}

/* root element for scrollable items */
.items {    
    position:absolute;
    
    /* this time we have very large space for height */ 
    height:20000em; 
    margin: 0px;
}

/* single scrollable item */
.item {
    border-bottom:1px solid #ddd;
    margin:10px 0;
    padding:15px;
    font-size:12px;
    height:180px;
}

/* elements inside single item */
.item img {
    float:left;
    margin-right:20px;
    height:180px;
    width:240px;
}

/* the action buttons above the scrollable */
#actions {
    width:500px;
    margin:30px 0 10px 0;   
}

#actions a {
    font-size:14px;     
    cursor:pointer;
    color:#EBEBEB;
}

#actions a:hover {
    text-decoration:underline;
    color:#EEF2B8;
}

.disabled {
    visibility:hidden;      
}

.next {
    float:right;
}   


 	</style>	
</head>

<body>
"""

    header_str += "<h1>"
    header_str += html_object.intro_text
    header_str += "</h1>"
    
    header_str += "<h3>Ions of Interest</h3>"
    header_str += "<ol>"
    for ioa in html_object.ions_of_interest:
        header_str += "<li>"
        header_str += ioa
        header_str += "</li>"
    header_str += "</ol>"

    header_str += "<h3>Run Key</h3>"

    id_file = sorted([(value,key) for (key,value) in html_object.experiment_index.items()])
    header_str += "<p>The experiments are keyed as follows</p>"
    for id_item in id_file:
        header_str +=  "<p>"
        header_str += id_item[0]
        header_str += " : "
        header_str += id_item[1]
        header_str += "</p>"

    if len( html_object.dict_of_tables) == 1:
        header_str += "<h3>Summary Table</h3>"
    else:
        header_str += "<h3>Summary Tables</h3>"

    table_keys = html_object.dict_of_tables.keys()
    table_keys.sort()
    for tk in table_keys:
        header_str += "<h4>"
        header_str += html_object.dict_of_tables[tk]['caption']
        header_str += "</h4>"
        header_str += '<table id=' + '"' + str(tk) + '">'
        header_str += html_object.dict_of_tables[tk]['table_html']
        header_str += "</table>"
    
    header_str += '<!-- OVERLAYS -->'
    #build overlays based on proteins by run.
    for rnd_k, vp_list in html_output.overlay_details.iteritems():
        header_str += '<div class="overlay_virtualprotein" id="' + rnd_k +'">'
        
        if len(vp_list) > 4:
            header_str += '''
            <div id="actions">
                <a class="prev">&laquo; Back</a>
                <a class="next">More Proteins &raquo;</a>
            </div>
            '''
        else:
            header_str += '''
            <div id="actions">
                <a class="prev disabled">&laquo; Back</a>
                <a class="next disabled">More Proteins &raquo;</a>
            </div>
            '''
        header_str += '<div class="scrollable vertical">'
        header_str += '<div class="items">'
        item_num = 0
        for vp in vp_list:
            if item_num % 4 == 0:
                header_str += '<div>'
            header_str += '<div class="item">'
            header_str += '<h2 rel="#' + vp + '">' + vp + '</h2>'
            header_str += '<h3>Names for Protein</h3>'
            for a_name in html_output.virtual_proteins[vp].names:
                header_str += '<p>' + a_name + '</p>'
            header_str += '</div>'
            if item_num % 4 == 0:
                header_str += '</div>'
            item_num += 1
        
        header_str += '</div>'
        header_str += '</div></div>'

    #build overlays based on virtual protein information
    for k_vpid, v_vp in html_output.virtual_proteins.iteritems():
        header_str += '<div class="overlay_virtualprotein" id="' + k_vpid + '">'
        header_str += '<h2>' + k_vpid + '</h2>'
        header_str += '<h3>Accessions</h3>'
        for item in v_vp.accessions:
            header_str += '<p>' + item + '</p>'
        header_str += '<h3>Protein Names</h3>'
        for item in v_vp.names:
            header_str += '<p>' + item + '</p>'
        header_str += '<h3>Significant Runs Up Regulated</h3>'
        for a_run in v_vp.significant_runs.keys():
            if len(v_vp.significant_runs[a_run]['up']) > 0:
                header_str += '<p>' + a_run + '</p>'
        
        header_str += '<h3>Significant Runs Down Regulated</h3>'
        for a_run in v_vp.significant_runs.keys():
            if len(v_vp.significant_runs[a_run]['down']) > 0:
                header_str += '<p>' + a_run + '</p>'


        header_str += '</div>'

    header_str += '''
    <script> 
        $("td[title]").tooltip( { position: "top right", offset: [0,-15], effect: "fade"  } );  
        $(document).ready(function() { 
            $("td[rel]").overlay();
            $("h2[rel]").overlay();
            $(".scrollable").scrollable({ vertical: true, mousewheel: true });  
            });
    </script>'    
    '''
    header_str += "</body></html>"
    
    return header_str


#----------------------------------------------------------------- Program Flow
sig_prot_run = RunSignificant()
virtual_proteins = dict()

run_index = dict()
num_runs = 0

f_in = open(sys.argv[1])

#Create the header index
h = f_in.readline().split('\t')

header = clean_entry(h)
ions_used = extract_ion_ids(header)

pvalue_index = [ header.index(x) for x in header if x.find('PVal') > -1]
for line in f_in.readlines():
    vals = clean_entry(line.strip().split('\t'))
    sig_prot_run.parse_data_line(vals, header, pvalue_index)

    run_id = vals[header.index('File Name')]
    if run_id in run_index:
        pass
    else:
        run_index[run_id] = string.letters[ num_runs + 26 ]
        num_runs += 1

    if vals[header.index('Virtual Protein ID')] in virtual_proteins:
        pass
    else:
        v_protein = VirtualProteins()
        v_protein.virtual_id = vals[header.index('Virtual Protein ID')]
        virtual_proteins[ vals[header.index('Virtual Protein ID')] ] = v_protein 
    tmp_vp = virtual_proteins[ vals[header.index('Virtual Protein ID')] ]
    tmp_vp.parse_data_line(vals, header, pvalue_index)

f_in.close()
ions_used.sort()

html_output = HtmlOutputDetail()
html_output.intro_text = "Summary Report for Significant Reporter Ion Regulation"
html_output.experiment_index = run_index
html_output.ions_of_interest = ions_used

(rep_ion_x_run, overlay_details) = build_ion_by_run(sig_prot_run.run_up, sig_prot_run.run_down, ions_used, run_index)
html_output.dict_of_tables[1]={ 'table_html': rep_ion_x_run, 
                                'caption': 'Reporter Ions with Significant Regulation by Experimental Run'}

rep_ion_x_protein = build_ion_by_protein(virtual_proteins, ions_used, run_index)
html_output.dict_of_tables[2]={ 'table_html': rep_ion_x_protein, 
                                'caption': 'Reporter Ions with Significant Regulation by Common Protein and Run'}
html_output.virtual_proteins = virtual_proteins
html_output.overlay_details = overlay_details

#print build_html_report(html_output)
with open(sys.argv[2], 'w') as f_out:
  f_out.write(build_html_report(html_output))


'''
#------------------------------------------------------------------------ NOTES
'''
