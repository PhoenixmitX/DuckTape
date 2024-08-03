set -e

user_home="/home/vscode"

add_to_bashrc() {
		echo "$1" >> $user_home/.bashrc
}
add_to_bashrc "" # fix potential missing newline at EOF

# some aliases
add_to_bashrc "alias ll='ls -alF'"
add_to_bashrc "alias la='ls -A'"

add_to_bashrc "alias dc='docker compose'"

# manually add docker bash completion
add_to_bashrc 'eval "$(docker completion bash)"'

# alias bash completion
curl -s --fail https://raw.githubusercontent.com/cykerway/complete-alias/master/complete_alias -o $user_home/.complete_alias
add_to_bashrc "source $user_home/.complete_alias"
add_to_bashrc 'complete -F _complete_alias "${!BASH_ALIASES[@]}"'
