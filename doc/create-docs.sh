
DOCNAME=configuration
DOCBOOK_XHTML_XSL=/usr/share/xml/docbook/stylesheet/nwalsh/xhtml/profile-docbook.xsl
DOCBOOK_FO_XSL=/usr/share/xml/docbook/stylesheet/nwalsh/fo/profile-docbook.xsl
cp $DOCNAME-raw.xml $DOCNAME.xml
DATE=`date +'%B %d, %Y'`
sed -i "s/PUBDATE/$DATE/g" $DOCNAME.xml
for AUDIENCE in grid local
do
  xsltproc --stringparam profile.audience $AUDIENCE -o $DOCNAME-$AUDIENCE.html $DOCBOOK_XHTML_XSL $DOCNAME.xml
  sed -i 's/<\/head>/<link rel="stylesheet" href="style.css" type="text\/css"  \/><\/head>/g' $DOCNAME-$AUDIENCE.html
  xsltproc --stringparam variablelist.as.blocks "1" --stringparam ulink.show "0" --stringparam profile.audience $AUDIENCE -o $DOCNAME-$AUDIENCE.fo $DOCBOOK_FO_XSL $DOCNAME.xml
  fop -pdf $DOCNAME-$AUDIENCE.pdf -fo $DOCNAME-$AUDIENCE.fo
done