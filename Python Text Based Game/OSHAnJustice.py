from colorama import Fore, init
init()


def logo():
    print("==========================================================================================================")
    print(" ██████╗ ███████╗██╗  ██╗ █████╗       ███╗   ██╗         ██╗██╗   ██╗███████╗████████╗██╗ ██████╗███████╗")
    print("██╔═══██╗██╔════╝██║  ██║██╔══██╗      ████╗  ██║         ██║██║   ██║██╔════╝╚══██╔══╝██║██╔════╝██╔════╝")
    print("██║   ██║███████╗███████║███████║█████╗██╔██╗ ██║         ██║██║   ██║███████╗   ██║   ██║██║     █████╗  ")
    print("██║   ██║╚════██║██╔══██║██╔══██║╚════╝██║╚██╗██║    ██   ██║██║   ██║╚════██║   ██║   ██║██║     ██╔══╝  ")
    print("╚██████╔╝███████║██║  ██║██║  ██║      ██║ ╚████║    ╚█████╔╝╚██████╔╝███████║   ██║   ██║╚██████╗███████╗")
    print(" ╚═════╝ ╚══════╝╚═╝  ╚═╝╚═╝  ╚═╝      ╚═╝  ╚═══╝     ╚════╝  ╚═════╝ ╚══════╝   ╚═╝   ╚═╝ ╚═════╝╚══════╝")
    print("==========================================================================================================")


def instructions():
    print(f'\n{Fore.BLUE}>>>OSHA-n Justice<<<{Fore.RESET}\n'
          f'Version: 0.0.2')
    print()
    print(f'>Make note of all 6 {Fore.YELLOW}hazards{Fore.RESET} before confronting the '
          f'{Fore.CYAN}Factory Owner{Fore.RESET} to win.')
    print(f'>Move commands: {Fore.RED}go{Fore.RESET} North, '
          f'{Fore.RED}go{Fore.RESET} South, '
          f'{Fore.RED}go{Fore.RESET} East, '
          f'{Fore.RED}go{Fore.RESET} West')
    print(f'>Add to notebook: {Fore.RED}note{Fore.RESET} hazard')
    print('>Type \'exit\' or \'quit\' to quit the game.\n'
          '>Type \'help\' to print these instructions again.')


def status():
    global current_room
    global notebook
    global rooms

    print('\n-----------------------------------')
    print(f'You are in the {Fore.RED}{current_room}{Fore.RESET}.')
    print(f'Notebook: {notebook}')
    if 'item' in rooms[current_room] and (rooms[current_room]['item'] != ''):
        if current_room == 'Machine Room':
            print(f'You see a broken {Fore.YELLOW}{rooms[current_room]["item"]}{Fore.RESET} being used.')
        elif current_room == 'Office':
            print(f'You come eye-to-eye with the {Fore.CYAN}{rooms[current_room]["item"]}{Fore.RESET}.')
        elif current_room == 'Stock Room':
            print(f'You see {Fore.YELLOW}{rooms[current_room]["item"]}{Fore.RESET} piled on a high shelf.')
        elif current_room == 'Break Room':
            print(f'You see missing {Fore.YELLOW}{rooms[current_room]["item"]}{Fore.RESET} in the floor.'
                  f' A tripping hazard.')
        elif current_room == 'Bathroom':
            print(f'You see a leaky {Fore.YELLOW}{rooms[current_room]["item"]}{Fore.RESET} under the sink.')
        elif current_room == 'Kitchen':
            print(f'You see {Fore.YELLOW}{rooms[current_room]["item"]}{Fore.RESET} growing in the fridge.')
        elif current_room == 'Smoking Area':
            print(f'You see rusty {Fore.YELLOW}{rooms[current_room]["item"]}{Fore.RESET} '
                  f'sticking out from loose boards.')
    print('-----------------------------------')


def play_again():
    print('Play again? Y or N')
    play = input('> ').strip('>' ' ')
    if play != 'Y' and play != 'y' and play != 'N' and play != 'n':
        print('Invalid command. Try again.')
        play_again()
    elif play == 'Y' or play == 'y':
        main()
    elif play == 'N' or play == 'n':
        print('Goodbye!')
        quit()


def main():
    global current_room
    global notebook
    global rooms

    notebook = []

    rooms = {
        'Parking Lot': {'south': 'Machine Room', 'item': ''},
        'Machine Room': {'north': 'Parking Lot', 'east': 'Office', 'south': 'Stock Room', 'item': 'sawblade'},
        'Office': {'west': 'Machine Room', 'item': 'Factory Owner'},
        'Stock Room': {'north': 'Machine Room', 'south': 'Smoking Area', 'west': 'Break Room', 'item': 'clutter'},
        'Break Room': {'east': 'Stock Room', 'north': 'Bathroom', 'south': 'Kitchen', 'item': 'tiles'},
        'Bathroom': {'south': 'Break Room', 'item': 'pipe'},
        'Kitchen': {'north': 'Break Room', 'east': 'Smoking Area', 'item': 'mold'},
        'Smoking Area': {'north': 'Stock Room', 'west': 'Kitchen', 'item': 'nails'}
    }
    current_room = 'Parking Lot'

    while True:
        status()
        if current_room == 'Office' and (len(notebook) < 6):
            print(f'\nYou failed to make note of all 6 {Fore.YELLOW}hazards{Fore.RESET} '
                  f'before confronting the {Fore.CYAN}Factory Owner{Fore.RESET}.'
                  f'\n{Fore.RED}You lose!{Fore.RESET}')
            play_again()
        elif current_room == 'Office' and (len(notebook) == 6):
            print(f'\nWith all 6 {Fore.YELLOW}hazards{Fore.RESET} found, you present the '
                  f'{Fore.CYAN}Factory Owner{Fore.RESET} with a fine for violations of OSHA guidelines. '
                  f'\n{Fore.GREEN}You win!{Fore.RESET}')
            play_again()

        user_command = input('> ').lower()
        if user_command == 'help':
            print()
            instructions()
        if (user_command == 'exit') or user_command == 'quit':
            quit()
        while user_command == '':
            print('You must input a command.\n')
            status()
            user_command = input('> ')
        try:
            command, key = user_command.split()
        except ValueError:
            if user_command != 'help':
                print('Invalid command')
        else:
            if command == 'go':
                if key in rooms[current_room]:
                    current_room = rooms[current_room][key]
                else:
                    print('There is nothing that way!')
            elif (command == 'note') and ('item' in rooms[current_room]):
                if ('item' in rooms[current_room]) and (key == rooms[current_room]['item']):
                    notebook.append(rooms[current_room]['item'])
                    print(f'You make note of the {Fore.YELLOW}{rooms[current_room]["item"]}{Fore.RESET}.')
                    rooms[current_room].pop('item')
                elif ('item' not in rooms[current_room]) or (key != rooms[current_room]['item']):
                    print('You don\'t see that.')


notebook = list
rooms = dict
current_room = str
logo()
instructions()
main()
