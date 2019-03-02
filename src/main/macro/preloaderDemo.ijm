// This demo script shows how image loading and processing can be
// parallelized: While image 1 is processed, image 2 is already 
// loading
// 
// Author: Robert Haase, MPI CBG, rhaase@mpi-cbg.de
// March 2019
// --------------------------------------------------------------

fileList1 = newArray(
	"https://bds.mpi-cbg.de/Timelapse_Drosophila_histone_RFP/tif/000200.raw.tif",
	"https://bds.mpi-cbg.de/Timelapse_Drosophila_histone_RFP/tif/000201.raw.tif",
	"https://bds.mpi-cbg.de/Timelapse_Drosophila_histone_RFP/tif/000202.raw.tif",
	"https://bds.mpi-cbg.de/Timelapse_Drosophila_histone_RFP/tif/000203.raw.tif",
	"https://bds.mpi-cbg.de/Timelapse_Drosophila_histone_RFP/tif/000204.raw.tif"
);

fileList2 = newArray(
	"https://bds.mpi-cbg.de/Timelapse_Drosophila_histone_RFP/tif/000210.raw.tif",
	"https://bds.mpi-cbg.de/Timelapse_Drosophila_histone_RFP/tif/000211.raw.tif",
	"https://bds.mpi-cbg.de/Timelapse_Drosophila_histone_RFP/tif/000212.raw.tif",
	"https://bds.mpi-cbg.de/Timelapse_Drosophila_histone_RFP/tif/000213.raw.tif",
	"https://bds.mpi-cbg.de/Timelapse_Drosophila_histone_RFP/tif/000214.raw.tif"
);

// -----------------------------------------------
// load images and process them sequentially
time = getTime();
for (i = 0; i < lengthOf(fileList1); i++) {
	filename = fileList1[i];
	loadStartTime = getTime();
	open(filename);
	deltaTime = getTime() - loadStartTime;
	print("Loading a single file took " + deltaTime + " msec");

	run("Mean 3D...", "x=3 y=3 z=3");
}
deltaTime = getTime() - time;
print("Loading " + lengthOf(fileList1) + " files and processing them took " + deltaTime + " msec");


// -----------------------------------------------
// load images with preloading while processing them
time = getTime();
for (i = 0; i < lengthOf(fileList2); i++) {
	filename = fileList2[i];
	if (i < lengthOf(fileList2) - 1) {
		nextFilename = fileList2[i + 1];
	}
	
	loadStartTime = getTime();
	run("Preloader", "current=" + filename + " next=" + nextFilename);
	deltaTime = getTime() - loadStartTime;
	print("Loading a single file with preloading took " + deltaTime + " msec");

	run("Mean 3D...", "x=3 y=3 z=3");
}
deltaTime = getTime() - time;
print("Loading " + lengthOf(fileList1) + " files with preloading and processing them took " + deltaTime + " msec" );


