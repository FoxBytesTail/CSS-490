import wave as w
from scipy.io import wavfile
from scipy.fftpack import fft
import matplotlib.pyplot as plt

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

b=[(ele/2**16.)*2-1 for ele in data] # this is 8-bit track, b is now normalized on [-1,1)
c = fft(data) # calculate fourier transform (complex numbers list)
d = len(c)/2  # you only need half of the fft list (real signal symmetry)

plt.subplot(2,1,2)
plt.title('Spectrum')
plt.xlabel("Frequency")
plt.ylabel("dB?")
plt.plot(abs(c[:(d-1)]),'r') 
plt.show()