import unittest
from Text2Int import text2int

class TestText2Int(unittest.TestCase):

	def test_simple_conversions(self):
		self.assertEqual(8, text2int('eight'))
		self.assertEqual(34, text2int('thirty four'))
		self.assertEqual(18, text2int('eighteen'))
		self.assertEquals(93, text2int('ninety three'))
		self.assertEqual(57, text2int('fifty seven'))

	def test_string_contains_and(self):
		self.assertEqual(340, text2int('three hundred and forty'))
		self.assertEqual(1345, text2int('one thousand three hundred and forty five'))

	'''
	def tests_string_contains_number_as_int(self):
		self.assertEqual(34, '34')
	'''

if __name__ == '__main__':
	unittest.main()