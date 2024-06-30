/*
 * Copyright (c) 2015-2020, Texas Instruments Incorporated
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * *  Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * *  Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 * *  Neither the name of Texas Instruments Incorporated nor the names of
 *    its contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/*
 *  ======== gpiointerrupt.c ========
 */
#include <stdint.h>
#include <stddef.h>
#include <stdio.h>

/* Driver Header files */
#include <ti/drivers/GPIO.h>
#include <ti/drivers/I2C.h>
#include <ti/drivers/UART2.h>
#include <ti/drivers/Timer.h>

/* Driver configuration */
#include "ti_drivers_config.h"

/* Initialize button states to not pressed */
int leftButton = 0; // Left button decrements setTemp
int rightButton = 0; // Right button increments setTemp

/*
 *  ======== gpioButtonFxn0 ========
 *  Callback function for the GPIO interrupt on CONFIG_GPIO_BUTTON_0.
 *
 *  Note: GPIO interrupts are cleared prior to invoking callbacks.
 */
void gpioButtonFxn0(uint_least8_t index)
{
    // When the left button is pressed, change it to 1
    leftButton = 1;
}

/*
 *  ======== gpioButtonFxn1 ========
 *  Callback function for the GPIO interrupt on CONFIG_GPIO_BUTTON_1.
 *  This may not be used for all boards.
 *
 *  Note: GPIO interrupts are cleared prior to invoking callbacks.
 */
void gpioButtonFxn1(uint_least8_t index)
{
    // When the right button is pressed, change it to 1
    rightButton = 1;
}

/*
 * ========= UART code ==========
 */
// My SDK uses UART2 so some of my functions vary (like UART2_write has an extra param)
size_t bytesWritten = 0;
#define DISPLAY(x) UART2_write(uart, &output, x, &bytesWritten);


// UART Global Variables
char output[64];
int bytesToSend;

// Driver Handles - Global variables
UART2_Handle uart;

void initUART(void)
{
    UART2_Params uartParams;

    // Configure the driver
    UART2_Params_init(&uartParams);
    uartParams.readReturnMode = UART2_ReadReturnMode_FULL;
    uartParams.baudRate = 115200;

    // Open the driver
    uart = UART2_open(CONFIG_UART2_0, &uartParams);

    if (uart == NULL) {
        /* UART_open() failed */
        while (1);
    }
}


/*
 * ========= I2C code ===========
 */
// I2C Global Variables
static const struct {
    uint8_t address;
    uint8_t resultReg;
    char *id;
} sensors[3] = {
                { 0x48, 0x0000, "11x" },
                { 0x49, 0x0000, "116" },
                { 0x41, 0x0001, "006" }
};
uint8_t txBuffer[1];
uint8_t rxBuffer[2];
I2C_Transaction i2cTransaction;

// Driver Handles - Global Variables
I2C_Handle i2c;

// Make sure you call initUART() before calling this function
void initI2C(void)
{
    int8_t i, found;
    I2C_Params i2cParams;

    DISPLAY(snprintf(output, 64, "Initializing I2C Driver - "))

    // Init the driver
    I2C_init();

    // Configure the driver
    I2C_Params_init(&i2cParams);
    i2cParams.bitRate = I2C_400kHz;

    // Open the driver
    i2c = I2C_open(CONFIG_I2C_0, &i2cParams);
    if (i2c == NULL)
    {
        DISPLAY(snprintf(output, 64, "Failed\n\r"))
            while (1);
    }

    DISPLAY(snprintf(output, 32, "Passed\n\r"))

    // Boards were shipped with different sensors.
    // Welcome to the world of embedded systems.
    // Try to determine which sensor we have.
    // Scan through the possible sensor addresses
    /* Common I2C transaction setup */
    i2cTransaction.writeBuf = txBuffer;
    i2cTransaction.writeCount = 1;
    i2cTransaction.readBuf = rxBuffer;
    i2cTransaction.readCount = 0;

    found = false;
    for (i=0; i<3; ++i)
    {
        i2cTransaction.targetAddress = sensors[i].address;
        txBuffer[0] = sensors[i].resultReg;

        DISPLAY(snprintf(output, 64, "Is this %s? ", sensors[i].id))
        if (I2C_transfer(i2c, &i2cTransaction))
        {
            DISPLAY(snprintf(output, 64, "Found\n\r"))
            found = true;
            break;
        }
        DISPLAY(snprintf(output, 64, "No\n\r"))
    }

    if(found)
    {
        DISPLAY(snprintf(output, 64, "Detected TMP%s I2C address: %x\n\r", sensors[i].id, i2cTransaction.targetAddress))
    }
    else
    {
        DISPLAY(snprintf(output, 64, "Temperature sensor not found, contact professor\n\r"))
    }
}

int16_t readTemp(void)
{
    int j;
    int16_t temperature = 0;

    i2cTransaction.readCount = 2;
    if (I2C_transfer(i2c, &i2cTransaction))
    {
        /*
        * Extract degrees C from the received data;
        * see TMP sensor datasheet
        */
        temperature = (rxBuffer[0] << 8) | (rxBuffer[1]);
        temperature *= 0.0078125;
        /*
        * If the MSB is set '1', then we have a 2's complement
        * negative value which needs to be sign extended
        */
        if (rxBuffer[0] & 0x80)
        {
            temperature |= 0xF000;
        }
    }
    else
    {
        DISPLAY(snprintf(output, 64, "Error reading temperature sensor (%d)\n\r",i2cTransaction.status))
        DISPLAY(snprintf(output, 64, "Please power cycle your board by unplugging USB and plugging back in.\n\r"))
    }

    return temperature;
}


/*
 * ========= Timer code ========
 */
// Driver Handles - Global variables
Timer_Handle timer0;

volatile unsigned char TimerFlag = 0;
void timerCallback(Timer_Handle myHandle, int_fast16_t status)
{
    TimerFlag = 1;
}
void initTimer(void)
{
    Timer_Params params;

    // Init the driver
    Timer_init();

    // Configure the driver
    Timer_Params_init(&params);
    params.period = 100000; // interrupt every 100ms
    params.periodUnits = Timer_PERIOD_US;
    params.timerMode = Timer_CONTINUOUS_CALLBACK;
    params.timerCallback = timerCallback;

    // Open the driver
    timer0 = Timer_open(CONFIG_TIMER_0, &params);

    if (timer0 == NULL) {
        /* Failed to initialized timer */
        while (1) {}
    }

    if (Timer_start(timer0) == Timer_STATUS_ERROR) {
        /* Failed to start timer */
        while (1) {}
    }
}


/*
 *  ======== mainThread ========
 */
void *mainThread(void *arg0)
{
    /* Call driver init functions */
    GPIO_init();

    /* Configure the LED and button pins */
    GPIO_setConfig(CONFIG_GPIO_LED_0, GPIO_CFG_OUT_STD | GPIO_CFG_OUT_LOW);
    GPIO_setConfig(CONFIG_GPIO_BUTTON_0, GPIO_CFG_IN_PU | GPIO_CFG_IN_INT_FALLING);

    /* Turn on user LED */
    GPIO_write(CONFIG_GPIO_LED_0, CONFIG_GPIO_LED_ON);

    /* Install Button callback */
    GPIO_setCallback(CONFIG_GPIO_BUTTON_0, gpioButtonFxn0);

    /* Enable interrupts */
    GPIO_enableInt(CONFIG_GPIO_BUTTON_0);

    /*
     *  If more than one input pin is available for your device, interrupts
     *  will be enabled on CONFIG_GPIO_BUTTON1.
     */
    if (CONFIG_GPIO_BUTTON_0 != CONFIG_GPIO_BUTTON_1)
    {
        /* Configure BUTTON1 pin */
        GPIO_setConfig(CONFIG_GPIO_BUTTON_1, GPIO_CFG_IN_PU | GPIO_CFG_IN_INT_FALLING);

        /* Install Button callback */
        GPIO_setCallback(CONFIG_GPIO_BUTTON_1, gpioButtonFxn1);
        GPIO_enableInt(CONFIG_GPIO_BUTTON_1);
    }

    /*
     * Thermostat Code
     */
    // Check buttons every 200ms
    // Check temp (I2C) every 500ms
    // Update LED and report to server every second (1000ms) (UART)
    // For the following orientations, the "top" of the board will be where the power supply plugs in
    // Pushing the left button will decrement the temperature by one every 200ms
    // Pushing the right button will increment the temperature by one every 200ms
    // If the read temperature is greater than the set temperature, turn off LED (heat off)
    // If the read temperature is less than the set temp, turn on LED (heat on)

    // General timer period will be 100ms as this is the LCD between 200, 500, and 1000ms intervals being used

    // UART output to the server should be formatted as <AA,BB,S,CCCC> where:
    // AA = ASCII decimal value of room temperature (00-99) deg Celsius
    // BB = ASCII decimal value of set temp (00-99) deg Celsius
    // S = '0' if heat is off, '1' if heat is on
    // CCCC = decimal count of seconds since board has been reset
    // <%02d,%02d,%d,%04d> = temperature, set temp, heat-status, seconds

    /* Init all drivers */
    initUART();
    initI2C();
    initTimer();

    /* Initialize all variables */
    unsigned long buttonCheckedTime = 0;      // Time since buttons were last read
    unsigned long tempCheckedTime = 0;        // Time since temperature was last read
    unsigned long displayCheckedTime = 0;     // Time since last LED update and data sent to server
    const unsigned long buttonPeriod = 200;   // Buttons are checked every 200ms
    const unsigned long tempPeriod = 500;     // Temperature is checked and the heat status LED updated every 500ms
    const unsigned long displayPeriod = 1000; // Data sent to server every 1000ms (1 second)
    const unsigned long timerPeriod = 100;    // LCD between 200, 500, and 1000ms is 100ms
    int setTemp = 22;                         // Default thermostat temp - 22C = ~72F - A nice room temperature default
    int heat = 0;                             // Heat is off by default - this will be updated (or not) depending on the first gathered I2C reading
    int seconds = 0;                          // Initialize CCCC value to 0 seconds passed
    int temperature = 0;                      // Initial temperature reading set to 0. This will update with the first I2C reading

    /* MAIN LOOP */
    while (1)
    {
        // Check buttons every 200ms
        if (buttonCheckedTime >= buttonPeriod) {
            // Reset time since buttons last checked
            buttonCheckedTime = 0;

            // If the left button is being pressed
            if (leftButton == 1) {
                // decrement setTemp
                setTemp -= 1;
                // reset button state for fresh read next cycle
                leftButton = 0;
            }

            // If the right button is being pressed
            if (rightButton == 1) {
                // increment setTemp
                setTemp += 1;
                // reset button state for fresh read next cycle
                rightButton = 0;
            }
        }

        // Check temperature (I2C) and update the heat status LED every 500ms
        if (tempCheckedTime >= tempPeriod) {
            // Reset time since temp last checked
            tempCheckedTime = 0;

            temperature = readTemp(); // Get temperature reading from I2C sensor

            // If the read temp is less than the setTemp,
            if (temperature < setTemp) {
                // Turn on the heat status LED (the room needs to be heated) and set heat to 1 (On)
                GPIO_write(CONFIG_GPIO_LED_0, CONFIG_GPIO_LED_ON);
                heat = 1;
            }
            // Otherwise
            else {
                // Turn off the heat status LED (the room does not need to be heated) and set heat to 0 (Off)
                GPIO_write(CONFIG_GPIO_LED_0, CONFIG_GPIO_LED_OFF);
                heat = 0;
            }
        }

        // Output data to UART every 1000ms
        if (displayCheckedTime >= displayPeriod) {
            // Reset Time since display last updated
            displayCheckedTime = 0;

            DISPLAY(snprintf(output, 64, "<%02d,%02d,%d,%04d>\n\r", temperature, setTemp, heat, seconds));
            seconds += 1; // Increment seconds variable every 1000ms when this branch executes
        }

        // Wait for next timer interrupt
        while (!TimerFlag) {}
        TimerFlag = 0; // Reset TimerFlag to wait for the interrupt after the current one

        // Increment the ***CheckedTime variables by the timerPeriod
        buttonCheckedTime += timerPeriod;
        tempCheckedTime += timerPeriod;
        displayCheckedTime += timerPeriod;
    }
    // The function never ends until execution is manually stopped or is stopped by a bug - therefore, we don't need a return
}
