"""
A test file for reading in data from a .wav file.
This program requires the scipy package to be installed.
"""
from scipy.io import wavfile
from scipy.fftpack import fft
import matplotlib.pyplot as plt
import numpy as n

#Returns an array of features
def extract_feature(data):
    pass

def get_energy(data):
    return n.cumsum(data**2)[-1]
    
def get_zeroCrossRate(data):
    sum = 0
    n = len(data)
    
    for i in xrange(n-1):
        global sign_a
        global sign_b
        sign_a = 1
        sign_b = -1
        
        if data[i] < 0:
            sign_a = -1
        if data[i+1] < 0:
            sign_b = 1
            
        sum = sum + sign_a + sign_b
        
    return sum/(2*(n-1))
    
def get_bandwidth(data):
    spec = abs(fft(data)[:(len(data)/2-1)])
    return max(spec) - min(spec)
    
def get_spectral_centroid(data):
    full_spectrum = abs(fft(data)[:(len(data)/2-1)])
    
    num = n.cumsum(full_spectrum * n.arange(len(full_spectrum)))[-1]
    denum = n.cumsum(n.arange(len(full_spectrum)))[-1]
    
    return num/denum
    
    
if __name__ == '__main__':
    fs, data = wavfile.read('./audio/music/mu1.wav')
    
    print "Sampling Frequency " + str(fs)
    print "Amplitudes " + str(data)
    print "Number of samples " + str(len(data))
    print data[0:16000:1000]
    
    plt.subplot(2,1,1)
    plt.title('Input File')
    plt.ylabel("Amplitude")
    plt.xlabel("Num Samples")
    plt.plot(data, 'g')


    get_spectral_centroid(data)
    
    c = fft(data) # calculate fourier transform (complex numbers list)
    d = len(c)/2  # you only need half of the fft list (real signal symmetry)

    plt.subplot(2,1,2)
    plt.title('Spectrum')
    plt.xlabel("Frequency")
    plt.ylabel("dB?")
    plt.plot(abs(c[:(d-1)]),'r') 
    plt.show()
    