/*
 *  Copyright 2010 Yuri Kanivets
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.edu.claf.wheel;

/**
 * 时间轮滑动监听接口
 */
public interface OnWheelScrollListener {
	/**
	 * 时间轮开始滑动监听接口
	 * @param wheel the wheel view whose state has changed.
	 */
	void onScrollingStarted(WheelView wheel);
	
	/**
	 * 时间轮滑动结束监听接口
	 * @param wheel the wheel view whose state has changed.
	 */
	void onScrollingFinished(WheelView wheel);
}
