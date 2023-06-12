#include "InvestmentCalculator.h"
#include <iostream>
#include <iomanip>
#include <vector>
using namespace std;


double initialInvestment;
double currentBalance;
double monthlyDeposit;
double interestRate;
int years;
int i;

int main() {
	// Get input variables from user
	cout << "Initial Investment Amount: ";
	cin >> initialInvestment;
	cout << "Monthly Deposit: ";
	cin >> monthlyDeposit;
	cout << "Annual Interest: ";
	cin >> interestRate;
	cout << "Number of years: ";
	cin >> years;

	// Create InvestmentCalculator object so we can call calculateYearEndBalance() function
	InvestmentCalculator investmentCalc;
	cout << endl << endl; // Clear some room below input text

	// Print table headers for both scenarios and then call calculateYearEndBalance() function for each scenario to calculate needed values and print to table
	cout << "Balance and Interest Without Additional Monthly Deposits" << endl;
	cout << setfill('=') << setw(56) << "=" << endl;
	investmentCalc.calculateYearEndBalance(0); // Table for monthly deposit of 0

	cout << endl << "Balance and Interest With Additional Monthly Deposits" << endl;
	cout << setfill('=') << setw(56) << "=" << endl;
	investmentCalc.calculateYearEndBalance(monthlyDeposit); // Table for monthly deposit of user input

	system("pause");

	return 0; // We did it!
}

void InvestmentCalculator::calculateYearEndBalance(double depositAmount) {
	vector<vector<double>> balancesAndInterestsVector(years); // For returning new balance and interest for each year on each function call
	currentBalance = initialInvestment;
	for (i = 0; i < years; i++) {
		int year = i + 1; // index starts at 0, year starts at 1
		double interestAccrued = 0; // Reset interestAccrued for each year
		// Compound interest every month, add deposit amount every month, and then total the interest accrued for our table
		for (int j = 0; j < 12; j++) {
			double monthInterest = 0; // New month, new interest
			monthInterest = (currentBalance + depositAmount) * ((interestRate / 100) / 12);
			interestAccrued += monthInterest;
			currentBalance += depositAmount + monthInterest; // Need to update currentBalance for each month
		}
		// Add year, year end balance, and year end interest to vector
		balancesAndInterestsVector[i].push_back(year);
		balancesAndInterestsVector[i].push_back(currentBalance);
		balancesAndInterestsVector[i].push_back(interestAccrued);
	}
	//Print out table
	cout << setfill(' ');
	cout << setw(5) << right << "Year" << " ";
	cout << setw(20) << right << "Year End Balance" << " ";
	cout << setw(28) << right << "Year End Earned Interest" << endl;
	cout << setfill('-') << setw(56) << "-" << endl;
	// Print out each year, year end balance, and year end interest FOR each year for which we have calculated values
	for (i = 0; i < years; i++) {
		// Output formatting to make our table pretty
		cout << setfill(' ');
		cout << setw(5) << right << fixed << setprecision(0) << balancesAndInterestsVector[i][0] << " ";
		cout << setw(20) << right << fixed << setprecision(2) << balancesAndInterestsVector[i][1] << " ";
		cout << setw(28) << right << fixed << setprecision(2) << balancesAndInterestsVector[i][2] << endl << endl;
	}
	// Note: This function could likely be broken up into smaller functions to make the code more readable but honestly, I had a migraine throbbing the entire time I was developing this and I just wanted to get it over with so I could take a nap. I'm sorry.
}
