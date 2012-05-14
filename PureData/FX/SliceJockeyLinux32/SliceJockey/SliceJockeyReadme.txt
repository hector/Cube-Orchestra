

Slice//Jockey package for Pd extended


- patches
- binaries [slicerec~] version 0.9.11 and [sliceplay~] version 0.9.12
- source code


///////////////////////////////////////////////////////////////////////////////////////////////////


INSTALL

In order to use Slice//Jockey, you should have Pd extended installed on your computer (http://puredata.info). Put the directory SliceJockey with all it's contents somewhere on your computer. Do not reorganize the directory content.


///////////////////////////////////////////////////////////////////////////////////////////////////


RUN

Start Pd extended. Select file >> open and load the patch SliceJockey.pd from directory SliceJockey. For help, click the question mark in the SliceJockey window. For settings, click the exclamation mark. Pay special attention to audio settings. To start slice-recording, activate one of the [>] buttons and feed sounds into your computer. 

For MacOSX and Linux, a gamepad-to-SliceJockey interface patch is included. Connect a gamepad to your computer before starting Pd extended. To use the interface you have to load the patch gamepad2SliceJockeyOSX.pd or gamepad2SliceJockeyLinux in addition to SliceJockey.pd.



///////////////////////////////////////////////////////////////////////////////////////////////////


DIRECTORY CONTENTS

The directory 'SJsessions' is for session recordings which are automatically stored there. You can safely rename recorded .wav files, play them with any soundfile player, or copy them to another location.

The directory 'slicestuff' contains binary object files for [slicerec~] and [sliceplay~], a help file for these classes, and an abstraction realtimeslicer.pd which is used in SliceJockey. The binary executable files have extensions according to platform:

.pd_linux for Linux
.pd_darwin for MacOSX
.dll for Windows

If you want to use [slicerec~] and [sliceplay~] in your own patches, create a path to the slicestuff directory or move the class binaries into a path known by Pd. In that case, be sure to delete any older versions of the binaries from your computer. See http://en.flossmanuals.net/PureData/AdvancedConfig for information about setting paths for Pd.

The directory 'source' contains source code for [slicerec~] and [sliceplay~].


///////////////////////////////////////////////////////////////////////////////////////////////////


SUPPORT

Slice//Jockey is documented with included help files, and a webpage at: 

http://www.katjaas.nl/slicejockey/slicejockey.html 

Individual support is not offered. For questions concerning installation and configuration of Pure Data, consult:
 
http://puredata.info/docs

Also consider searching the forum and the Pd mailing list archives: 

http://puredata.hurleur.com 
http://lists.puredata.info/pipermail/pd-list 

If you find a bug in [slicerec~], [sliceplay~] or the patch [SliceJockey], please send an email to katjavetter@gmail.com.


///////////////////////////////////////////////////////////////////////////////////////////////////


LICENSING

Patches SliceJockey.pd, realtimeslicer.pd, gamepad2SliceJockeyOSX and gamepad2SliceJockeyLinux are published under GPL. Copyright (c) 2011, Katja Vetter. The patches are distributed in the hope that they will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this package. If not, see <http://www.gnu.org/licenses/>.


Pure Data classes slicerec~ and sliceplay~ are published under 3-clause BSD license:

Copyright (c) 2009 - 2011, Katja Vetter
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

- Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.

- Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.

- Neither the name of the author nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.


///////////////////////////////////////////////////////////////////////////////////////////////////


sliceplay~ version history

version 0.9.12:
- addition of 'interrupt' method and fade-out of interrupted slice
- addition of method and message selector 'minspeed' for minimum playback speed
- addition of play status message outlet
- negative cuepoints are now ignored, not interpreted modulo-loopsize


version 0.9.11: 
- used garray_getfloatwords() instead of garray_getfloatarray(), for 64 bit compatibility


version 0.91:
- fixed bug with playback speed
- amplitude compensation is now variable user parameter


version 0.9: first version


///////////////////////////////////////////////////////////////////////////////////////////////////


slicerec~ version history

version 0.9.11: 
- function slicerec_fadeout() renewed and called from slicerec_analysis()
- message selector 'start' added, for manual slice-recording start 


version 0.9.1: 
- used garray_getfloatwords() instead of garray_getfloatarray(), for 64 bit compatibility


version 0.9: first version

