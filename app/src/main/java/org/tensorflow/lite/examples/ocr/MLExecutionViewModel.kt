/* Copyright 2021 The TensorFlow Authors. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
==============================================================================
*/

package org.tensorflow.lite.examples.ocr

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExecutorCoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

private const val TAG = "MLExecutionViewModel"

class MLExecutionViewModel : ViewModel() {

  private val _resultingBitmap = MutableLiveData<ModelExecutionResult>()

  val resultingBitmap: LiveData<ModelExecutionResult>
    get() = _resultingBitmap

  private val viewModelJob = Job()
  private val viewModelScope = CoroutineScope(viewModelJob)

  // the execution of the model has to be on the same thread where the interpreter
  // was created
  fun onApplyModel(
    ocrModel: OCRModelExecutor?,
    inferenceThread: ExecutorCoroutineDispatcher,
    tfImageView : Bitmap
  ) {
    viewModelScope.launch(inferenceThread) {
      try {
        val result = ocrModel?.execute(tfImageView)
        _resultingBitmap.postValue(result)
      } catch (e: Exception) {
        Log.e(TAG, "Fail to execute OCRModelExecutor: ${e.message}")
        _resultingBitmap.postValue(null)
      }
    }
  }
}
