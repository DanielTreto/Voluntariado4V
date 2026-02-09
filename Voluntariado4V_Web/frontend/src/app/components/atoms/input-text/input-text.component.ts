import { Component, Input, forwardRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';

@Component({
    selector: 'app-input-text',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './input-text.component.html',
    providers: [
        {
            provide: NG_VALUE_ACCESSOR,
            useExisting: forwardRef(() => InputTextComponent),
            multi: true
        }
    ]
})
export class InputTextComponent implements ControlValueAccessor {
    @Input() label: string = '';
    @Input() placeholder: string = '';
    @Input() type: string = 'text';
    @Input() errorMessage: string = '';
    @Input() isInvalid: boolean = false;

    value: string = '';
    isDisabled: boolean = false;

    onChange = (value: string) => { };
    onTouched = () => { };

    onInput(event: Event) {
        const value = (event.target as HTMLInputElement).value;
        this.value = value;
        this.onChange(value);
    }

    writeValue(value: string): void {
        this.value = value;
    }

    registerOnChange(fn: any): void {
        this.onChange = fn;
    }

    registerOnTouched(fn: any): void {
        this.onTouched = fn;
    }

    setDisabledState(isDisabled: boolean): void {
        this.isDisabled = isDisabled;
    }
}
