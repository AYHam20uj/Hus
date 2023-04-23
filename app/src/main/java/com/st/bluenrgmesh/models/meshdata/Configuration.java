/**
 * *****************************************************************************
 *
 * @file Configuration.java
 * @author BLE Mesh Team
 * @version V1.11.000
 * @date    20-October-2019
 * @brief User Application file
 * *****************************************************************************
 * @attention <h2><center>&copy; COPYRIGHT(c) 2017 STMicroelectronics</center></h2>
 * <p>
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 3. Neither the name of STMicroelectronics nor the names of its contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 * <p>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * <p>
 * BlueNRG-Mesh is based on Motorola’s Mesh Over Bluetooth Low Energy (MoBLE)
 * technology. STMicroelectronics has done suitable updates in the firmware
 * and Android Mesh layers suitably.
 * <p>
 * *****************************************************************************
 */
package com.st.bluenrgmesh.models.meshdata;

import java.io.Serializable;
import java.util.ArrayList;


public class Configuration implements Serializable {

    private ArrayList<NetKey2> netKeys;

    public ArrayList<NetKey2> getNetKeys() { return this.netKeys; }

    public void setNetKeys(ArrayList<NetKey2> netKeys) { this.netKeys = netKeys; }

    private ArrayList<AppKey2> appKeys;

    public ArrayList<AppKey2> getAppKeys() { return this.appKeys; }

    public void setAppKeys(ArrayList<AppKey2> appKeys) { this.appKeys = appKeys; }

    private NetworkTransmit networkTransmit;

    public NetworkTransmit getNetworkTransmit() { return this.networkTransmit; }

    public void setNetworkTransmit(NetworkTransmit networkTransmit) { this.networkTransmit = networkTransmit; }

    private int defaultTTL;

    public int getDefaultTTL() { return this.defaultTTL; }

    public void setDefaultTTL(int defaultTTL) { this.defaultTTL = defaultTTL; }

    private Features2 features;

    public Features2 getFeatures() { return this.features; }

    public void setFeatures(Features2 features) { this.features = features; }

    private RelayRetransmit relayRetransmit;

    public RelayRetransmit getRelayRetransmit() { return this.relayRetransmit; }

    public void setRelayRetransmit(RelayRetransmit relayRetransmit) { this.relayRetransmit = relayRetransmit; }


}
