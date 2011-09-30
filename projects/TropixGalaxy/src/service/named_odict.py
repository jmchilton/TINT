from odict import odict

class named_odict(odict):
  def __init__(self, name = "", dict = None):
    self.name = name
    odict.__init__(self, dict)

  def __str__(self):
    return self.name
   
