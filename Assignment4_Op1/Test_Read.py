"""
A test file for reading in data from a .wav file.
This program requires the scipy package to be installed.
"""
from scipy.io import wavfile
from scipy.fftpack import fft
import matplotlib.pyplot as plt
import numpy as n

#Returns an array of features
def extract_features(data):
    energy = get_energy(data)
    cross_rate = get_zeroCrossRate(data)
    bandwidth = get_bandwidth(data)
    spectral_centroid = get_spectral_centroid(data)
    
    feature_array = n.zeros((1,4), dtype='f')
    
    for row in xrange(n.shape(feature_array)[0]):
        feature_array[row][0] = energy
        feature_array[row][1] = cross_rate
        feature_array[row][2] = bandwidth
        feature_array[row][3] = spectral_centroid
        
    print
    print "Feature Array"
    print feature_array
            
    
    
def get_energy(data):
    return n.cumsum(data**2)[-1]/len(data)
    
def get_zeroCrossRate(data):
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
    
def get_bandwidth(data):
    fft_data = fft(data)
    
    firstPeak = max(fft_data[0:len(fft_data)/2])
    secondPeak = max(fft_data[len(fft_data)/2:])
    
    l_data = list(fft_data)
    min_freq = l_data.index(firstPeak) 
    l_data.remove(firstPeak)
    max_freq = l_data.index(secondPeak) + 1
    
    return max_freq - min_freq
        
def get_spectral_centroid(data):
    full_spectrum = abs(fft(data)[:(len(data)/2-1)])
    
    num = n.cumsum(full_spectrum * n.arange(len(full_spectrum)))[-1]
    denum = n.cumsum(n.arange(len(full_spectrum)))[-1]
    
    return num/denum
    
    
if __name__ == '__main__':
    fs, data = wavfile.read('./audio/music/mu1.wav')
    #fs, data = wavfile.read('./audio/speech/sp3.wav')
    
    print "Sampling Frequency " + str(fs)
    print "Amplitudes " + str(data)
    print "Number of samples " + str(len(data))
    
    plt.subplot(3,1,1)
    plt.title('Input File')
    plt.ylabel("Amplitude")
    plt.xlabel("Num Samples")
    plt.plot(data, 'g')

    extract_features(data)
    
    c = fft(data) # calculate fourier transform (complex numbers list)
    d = len(c)/2  # you only need half of the fft list (real signal symmetry)

    plt.subplot(3,1,2)
    plt.title('Spectrum')
    plt.xlabel("Frequency")
    plt.ylabel("dB?")
    plt.plot(c[:(len(c)-1)],'r')     
    
    plt.show()
    
    