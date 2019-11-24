#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Mon Aug 26 22:02:51 2019

@author: thinhnguyen
"""

# Luong nam
annual_salary = float(input('Enter your annual salary : '))

# Phan tram luong thang de danh
portion_saved_rate = float(input('Enter the percent of your salary to save, as a decimal:​ '))

# Gia nha
total_cost = float(input('Enter the cost of your dream home:​ '))

# Tien dat coc = 25 % gia nha
portion_down_payment = 0.25 * total_cost

# Luong thang
monthly_salary = annual_salary / 12

# Tien trich tu luong thang
portion_saved = monthly_salary * portion_saved_rate

# Tien da de danh
current_savings = float(0)


months = 0

while True:
    months = months + 1
    
    # Tien dau tu
    investments = current_savings * 0.04 / 12
    
    current_savings = current_savings + investments + portion_saved
    
    if(current_savings >= portion_down_payment):
        break


print('Number of months:​ ', months)