"""
A test file for reading in data from a .wav file.
This program requires the scipy package to be installed.
"""
from scipy.io import wavfile
from scipy.fftpack import fft
import matplotlib.pyplot as plt
import numpy as n

class AudioClassifier():
    
    def __init__(self):
        self.constants = {'FILE_NAME' : 0, 'ENERGY' : 1, 'CROSS_RATE' : 2, 
                        'BANDWIDTH' : 3, 'SPECTRAL_CENTROID' : 4, 'LABEL' : 5} 
                        
        self.training_ratio = 2/3
        self.classifying_ratio = 1/3
                        
        self.speech_file_list = self.read_files('SPEECH')
        self.audio_file_list = self.read_files('AUDIO')
        
        self.feature_array = n.zeros((len(self.speech_file_list + self.audio_file_list), 
                                                            len(self.constants)), dtype='f')    
        
    
    def classify(self, file_list):
        self.extract_features(False)
    
    def gen_model(self, file_list):
        self.extract_features()
        
    
    #Returns an array of features
    def extract_features(self, training = True):
        
        for f in self.audio_file_list:
            fs, data = wavfile.read('./audio/music/mu1.wav')
            fs, data = wavfile.read(f)
            
            energy = self.get_energy(data)
            cross_rate = self.get_zeroCrossRate(data)
            bandwidth = self.get_bandwidth(data)
            spectral_centroid = self.get_spectral_centroid(data)
            
            for row in xrange(n.shape(self.feature_array)[0]):
                self.feature_array[row][self.constants['FILE_NAME']] = f
                self.feature_array[row][self.constants['ENERGY']] = energy
                self.feature_array[row][self.constants['CROSS_RATE']] = cross_rate
                self.feature_array[row][self.constants['BANDWIDTH']] = bandwidth
                self.feature_array[row][self.constants['SPECTRAL_CENTROID']] = spectral_centroid
                self.feature_array[row][self.constants['LABEL']] = 'yes' if training else self.classify(f)
                
        for f in self.speech_file_list:
            fs, data = wavfile.read(f)
            
            energy = self.get_energy(data)
            cross_rate = self.get_zeroCrossRate(data)
            bandwidth = self.get_bandwidth(data)
            spectral_centroid = self.get_spectral_centroid(data)
            
            for row in xrange(n.shape(self.feature_array)[0]):
                self.feature_array[row][self.constants['FILE_NAME']] = f
                self.feature_array[row][self.constants['ENERGY']] = energy
                self.feature_array[row][self.constants['CROSS_RATE']] = cross_rate
                self.feature_array[row][self.constants['BANDWIDTH']] = bandwidth
                self.feature_array[row][self.constants['SPECTRAL_CENTROID']] = spectral_centroid
                self.feature_array[row][self.constants['LABEL']] = 'no' if training else self.classify(f) 
                
        print self.feature_array
                
        
        
    def get_energy(self, data):
        return n.cumsum(data**2)[-1]/len(data)
        
    def get_zeroCrossRate(self, data):
        sum = 0.0
        n = len(data)
        
        #zero cross rate
        #(1 - 1) + ... for positive, positive
        #(1 + 1) + ... for positive, negative
        #(-1 -1) + ... for negative, positive
        #(-1 + 1) + ... for negative, negative
    
        for i in xrange(n-1):
            global sign_a
            global sign_b
            sign_a = 1
            sign_b = -1
            
            if data[i] < 0:
                sign_a = -1
            if data[i+1] < 0:
                sign_b = 1
            
            sum = sum + abs(sign_a + sign_b)
            
        return sum/(2.0*(n-1))
        
    def get_bandwidth(self, data):
        fft_data = fft(data)
        
        firstPeak = max(fft_data[0:len(fft_data)/2])
        secondPeak = max(fft_data[len(fft_data)/2:])
        
        l_data = list(fft_data)
        min_freq = l_data.index(firstPeak) 
        l_data.remove(firstPeak)
        max_freq = l_data.index(secondPeak) + 1
        
        return max_freq - min_freq
            
    def get_spectral_centroid(self, data):
        full_spectrum = abs(fft(data)[:(len(data)/2-1)])
        
        num = n.cumsum(full_spectrum * n.arange(len(full_spectrum)))[-1]
        denum = n.cumsum(n.arange(len(full_spectrum)))[-1]
        
        return num/denum
        
    def read_files(self):
    	file_list = []
    	#load the music files
       	for x in range(0, 20):
       		path = './audio/music/mu'
       		number = x
       		extension = '.wav'
       		file_list.append(path + number + extension)

       	#load the speech files
       	for x in range(20, 40):
       		path = './audio/music/sp'
       		number = x
       		extension = '.wav'
       		file_list.append(path + number + extension)

       	#index 0 - 19 are music files
       	#index 20 - 39 are speech files
       	return file_list
        
    
if __name__ == '__main__':
    fs, data = wavfile.read('./audio/music/mu1.wav')
    #fs, data = wavfile.read('./audio/speech/sp3.wav')
    
    aclassifer = AudioClassifier()
    aclassifer.extract_features(data)
    
    #print "Sampling Frequency " + str(fs)
    #print "Amplitudes " + str(data)
    #print "Number of samples " + str(len(data))
    
    """
    plt.subplot(3,1,1)
    plt.title('Input File')
    plt.ylabel("Amplitude")
    plt.xlabel("Num Samples")
    plt.plot(data, 'g')

    
    c = fft(data) # calculate fourier transform (complex numbers list)
    d = len(c)/2  # you only need half of the fft list (real signal symmetry)

    plt.subplot(3,1,2)
    plt.title('Spectrum')
    plt.xlabel("Frequency")
    plt.ylabel("d?")
    plt.plot(c[:(len(c)-1)],'r')     
    
    plt.show()
    """
    